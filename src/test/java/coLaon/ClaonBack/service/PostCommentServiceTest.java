package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.OperatingTime;
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
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {
    @Mock
    PostCommentRepository postCommentRepository;
    @Mock
    PostCommentRepositorySupport postCommentRepositorySupport;
    @Mock
    PostRepository postRepository;
    @Mock
    BlockUserRepository blockUserRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostCommentService postCommentService;

    private User writer, writer2, blockedUser, privateUser;
    private Post post, blockedPost, privatePost;
    private PostComment postComment, postComment2, privateComment;
    private PostComment childPostComment, childPostComment2, childPostComment3, childPostComment4;
    private BlockUser blockUser;

    @BeforeEach
    void setUp() {
        this.writer = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.writer, "id", "testUserId");

        this.writer2 = User.of(
                "test123@gmail.com",
                "1234567890",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.writer2, "id", "testUserId2");

        this.blockedUser = User.of(
                "test123@gmail.com",
                "test2345!!",
                "blockUser",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.blockedUser, "id", "blockUserId");

        this.blockUser = BlockUser.of(
                writer,
                blockedUser
        );

        privateUser = User.of(
                "test123@gmail.com",
                "test2345!!",
                "privateUser",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        privateUser.changePublicScope();
        ReflectionTestUtils.setField(privateUser, "id", "privateUserId");

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
                "hold info img test"
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

        this.blockedPost = Post.of(
                center,
                "testContent3",
                blockedUser,
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(this.blockedPost, "id", "blockedPostId");
        ReflectionTestUtils.setField(this.blockedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.blockedPost, "updatedAt", LocalDateTime.now());

        this.privatePost = Post.of(
                center,
                "testContent4",
                privateUser,
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(this.privatePost, "id", "privatePostId");
        ReflectionTestUtils.setField(this.privatePost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.privatePost, "updatedAt", LocalDateTime.now());

        this.privateComment = PostComment.of(
                "testContent1",
                writer,
                privatePost,
                null
        );
        ReflectionTestUtils.setField(this.privateComment, "id", "privateCommentId");
        ReflectionTestUtils.setField(this.privateComment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.privateComment, "updatedAt", LocalDateTime.now());
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

            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

            mockedPostComment.when(() -> PostComment.of(
                    "testContent1",
                    this.writer,
                    this.post,
                    null
            )).thenReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment(writer, "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content")
                    .contains(false, "testContent1");
        }
    }

    @Test
    @DisplayName("Success case for create parent comment for own post")
    void successCreateParentCommentForOwnPost() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                    "testContent1",
                    null
            );

            given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

            mockedPostComment.when(() -> PostComment.of(
                    "testContent1",
                    this.privateUser,
                    this.privatePost,
                    null
            )).thenReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment(privateUser, "privatePostId", commentRequestDto);

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
            CommentResponseDto commentResponseDto = this.postCommentService.createComment(writer, "testPostId", commentRequestDto);

            // then
            assertThat(commentResponseDto)
                    .isNotNull()
                    .extracting("isDeleted", "content", "parentCommentId")
                    .contains(false, "testChildContent1", "testCommentId");
        }
    }

    @Test
    @DisplayName("Failure case for create parent comment for private user")
    void failureCreateParentCommentForPrivateUser() {
        // given
        CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                "testContent1",
                null
        );

        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.createComment(writer, "privatePostId", commentRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
    }

    @Test
    @DisplayName("Failure case for create parent comment for blocked user")
    void failureCreateParentCommentForBlockedUser() {
        // given
        CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto(
                "testContent1",
                null
        );

        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.createComment(writer, "blockedPostId", commentRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
    }

    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        Page<CommentFindResponseDto> parents = new PageImpl<>(
                List.of(
                        new CommentFindResponseDto(postComment, 0, writer.getNickname()),
                        new CommentFindResponseDto(postComment2, 0, writer.getNickname())
                ),
                pageable,
                2
        );

        given(this.postCommentRepositorySupport.findParentCommentByPost(post.getId(), writer.getId(), writer.getNickname(), pageable)).willReturn(parents);

        // when
        Pagination<CommentFindResponseDto> commentFindResponseDto = this.postCommentService.findCommentsByPost(writer, "testPostId", pageable);

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
    @DisplayName("Success case for find parent comments for own post")
    void successFindParentCommentsForOwnPost() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

        Page<CommentFindResponseDto> parents = new PageImpl<>(
                List.of(
                        new CommentFindResponseDto(postComment, 0, writer.getNickname()),
                        new CommentFindResponseDto(postComment2, 0, writer.getNickname())
                ),
                pageable,
                2
        );

        given(this.postCommentRepositorySupport.findParentCommentByPost(privatePost.getId(), privateUser.getId(), privateUser.getNickname(), pageable)).willReturn(parents);

        // when
        Pagination<CommentFindResponseDto> commentFindResponseDto = this.postCommentService.findCommentsByPost(privateUser, "privatePostId", pageable);

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
    @DisplayName("Failure case for find parent comment for private user")
    void failureFindParentCommentForPrivateUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.findCommentsByPost(writer, "privatePostId", pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
    }

    @Test
    @DisplayName("Failure case for find parent comment for blocked user")
    void failureFindParentCommentForBlockedUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.findCommentsByPost(writer, "blockedPostId", pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<PostComment> children = new PageImpl<>(List.of(childPostComment, childPostComment2), pageable, 2);

        given(this.postCommentRepository.findByIdAndIsDeletedFalse("testCommentId")).willReturn(Optional.of(postComment));
        given(this.postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), writer.getId(), pageable)).willReturn(children);

        // when
        Pagination<ChildCommentResponseDto> commentFindResponseDto = this.postCommentService.findAllChildCommentsByParent(writer, "testCommentId", pageable);

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
    @DisplayName("Success case for find child comments for own post")
    void successFindChildCommentsForOwnPost() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<PostComment> children = new PageImpl<>(List.of(childPostComment, childPostComment2), pageable, 2);

        given(this.postCommentRepository.findByIdAndIsDeletedFalse("privateCommentId")).willReturn(Optional.of(privateComment));
        given(this.postCommentRepositorySupport.findChildCommentByParentComment(privateComment.getId(), privateUser.getId(), pageable)).willReturn(children);

        // when
        Pagination<ChildCommentResponseDto> commentFindResponseDto = this.postCommentService.findAllChildCommentsByParent(privateUser, "privateCommentId", pageable);

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
        given(this.postCommentRepository.findById("testChildId1")).willReturn(Optional.of(childPostComment));

        given(this.postCommentRepository.save(childPostComment)).willReturn(childPostComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.deleteComment(writer, "testChildId1");

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
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.deleteComment(writer2, "testCommentId")
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

        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        given(this.postCommentRepository.save(postComment)).willReturn(postComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.updateComment(writer, "testCommentId", commentUpdateRequestDto);

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

        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.postCommentService.updateComment(writer2, "testCommentId", commentUpdateRequestDto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}
