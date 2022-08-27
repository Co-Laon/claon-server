package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.repository.PostCommentRepositorySupport;
import coLaon.ClaonBack.post.service.PostCommentService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostCommentRepository postCommentRepository;
    @Mock
    PostCommentRepositorySupport postCommentRepositorySupport;
    @Mock
    PostRepository postRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostCommentService postCommentService;

    private User writer, writer2;
    private Post post;
    private PostComment postComment, postComment2;
    private PostComment childPostComment, childPostComment2, childPostComment3, childPostComment4;

    @BeforeEach
    void setUp() {
        this.writer = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.writer, "id", "testUserId");

        this.writer2 = User.of(
                "test123@gmail.com",
                "1234567890",
                "test2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.writer2, "id", "testUserId2");

        Center center = Center.of(
                "testCenter",
                "testAddress",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );
        ReflectionTestUtils.setField(center, "id", "center1");

        this.post = Post.of(
                center,
                "testContent",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                writer
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");

        this.postComment = PostComment.of(
                "testContent1",
                writer,
                post,
                null
        );
        ReflectionTestUtils.setField(this.postComment, "id", "testCommentId");
        ReflectionTestUtils.setField(this.postComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.postComment, "updatedAt", LocalDateTime.now());

        this.childPostComment = PostComment.of(
                "testChildContent1",
                writer,
                post,
                postComment
        );
        ReflectionTestUtils.setField(this.childPostComment, "id", "testChildId1");
        ReflectionTestUtils.setField(this.childPostComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.childPostComment, "updatedAt", LocalDateTime.now());

        this.childPostComment2 = PostComment.of(
                "testChildContent2",
                writer,
                post,
                postComment
        );
        ReflectionTestUtils.setField(this.childPostComment2, "id", "testChildId2");
        ReflectionTestUtils.setField(this.childPostComment2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.childPostComment2, "updatedAt", LocalDateTime.now());

        this.postComment2 = PostComment.of(
                "testContent2",
                writer,
                post,
                null
        );
        ReflectionTestUtils.setField(this.postComment2, "id", "testCommentId2");
        ReflectionTestUtils.setField(this.postComment2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.postComment2, "updatedAt", LocalDateTime.now());

        this.childPostComment3 = PostComment.of(
                "testChildContent3",
                writer,
                post,
                postComment2
        );
        ReflectionTestUtils.setField(this.childPostComment3, "id", "testChildId3");
        ReflectionTestUtils.setField(this.childPostComment3, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.childPostComment3, "updatedAt", LocalDateTime.now());

        this.childPostComment4 = PostComment.of(
                "testChildContent4",
                writer,
                post,
                postComment
        );
        ReflectionTestUtils.setField(this.childPostComment4, "id", "testChildId4");
        ReflectionTestUtils.setField(this.childPostComment4, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.childPostComment4, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                    "testContent1",
                    null
            );

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

            mockedPostComment.when(() -> PostComment.of(
                    "testContent1",
                    this.writer,
                    this.post,
                    null
            )).thenReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content")
                    .contains(false, "testContent1");
        }
    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testChildContent1", postComment.getId());

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

            mockedPostComment.when(() -> PostComment.of(
                    "testChildContent1",
                    this.writer,
                    this.post,
                    postComment
            )).thenReturn(this.childPostComment);

            given(this.postCommentRepository.save(this.childPostComment)).willReturn(this.childPostComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content", "parentCommentId")
                    .contains(false, "testChildContent1", "testCommentId");
        }
    }

    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        Page<PostComment> parents = new PageImpl<>(List.of(postComment, postComment2), pageable, 2);
        Page<PostComment> children1 = new PageImpl<>(List.of(childPostComment, childPostComment2, childPostComment4), pageable, 2);
        Page<PostComment> children2 = new PageImpl<>(List.of(childPostComment3), pageable, 2);

        given(this.postCommentRepositorySupport.findParentCommentByPost(post.getId(), writer.getId(), pageable)).willReturn(parents);
        given(this.postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), writer.getId(), pageable)).willReturn(children1);
        given(this.postCommentRepositorySupport.findChildCommentByParentComment(postComment2.getId(), writer.getId(), pageable)).willReturn(children2);

        // when
        Pagination<CommentFindResponseDto> commentFindResponseDto = this.postCommentService.findCommentsByPost("testUserId", "testPostId", pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(CommentFindResponseDto::getContent, CommentFindResponseDto::getCommentId)
                .containsExactly(
                        tuple("testContent1", "testCommentId"),
                        tuple("testContent2", "testCommentId2")
                );
    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<PostComment> children = new PageImpl<>(List.of(childPostComment, childPostComment2), pageable, 2);

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findByIdAndIsDeletedFalse("testCommentId")).willReturn(Optional.of(postComment));
        given(this.postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), writer.getId(), pageable)).willReturn(children);

        // when
        Pagination<ChildCommentResponseDto> commentFindResponseDto = this.postCommentService.findAllChildCommentsByParent("testUserId", "testCommentId", pageable);

        // then
        assertThat(commentFindResponseDto.getResults())
                .isNotNull()
                .extracting(ChildCommentResponseDto::getContent, ChildCommentResponseDto::getIsDeleted)
                .containsExactly(
                        tuple("testChildContent1", false),
                        tuple("testChildContent2", false)
                );
    }

    @Test
    @DisplayName("Success case for delete comment")
    void successDeleteComment() {
        // given
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findById("testChildId1")).willReturn(Optional.of(childPostComment));

        given(this.postCommentRepository.save(childPostComment)).willReturn(childPostComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.deleteComment("testUserId", "testChildId1");

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "isDeleted")
                .contains("testChildId1", true);
    }

    @Test
    @DisplayName("Failure case for delete comment because delete by other user")
    void failDeleteComment_Unauthorized() {
        // given
        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(writer2));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.deleteComment("testUserId2", "testCommentId")
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for update comment")
    void successUpdateComment() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        given(this.postCommentRepository.save(postComment)).willReturn(postComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.updateComment("testUserId", "testCommentId", commentUpdateRequestDto);

        // then
        assertThat(commentResponseDto)
                .isNotNull()
                .extracting("commentId", "content")
                .contains("testCommentId", "updateContent");
    }

    @Test
    @DisplayName("Failure case for update comment because update by other user")
    void failUpdateComment_Unauthorized() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent");

        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(writer2));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.updateComment("testUserId2", "testCommentId", commentUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}
