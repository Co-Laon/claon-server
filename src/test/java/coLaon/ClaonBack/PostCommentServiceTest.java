package coLaon.ClaonBack;

import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.Service.PostCommentService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    PostRepository postRepository;

    @InjectMocks
    PostCommentService postCommentService;

    private User writer;
    private User writer2;
    private Post post;
    private PostComment postComment;
    private PostComment postComment2;
    private PostComment childPostComment;
    private PostComment childPostComment2;
    private PostComment childPostComment3;

    @BeforeEach
    void setUp() {
        this.writer = User.of(
                "testUserId",
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.writer2 = User.of(
                "testUserId2",
                "test123@gmail.com",
                "1234567890",
                "test2",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.post = Post.of(
                "testPostId",
                "center1",
                "hold",
                "testContent",
                writer,
                null
        );

        this.postComment = PostComment.of(
                "testCommentId",
                "testContent1",
                writer,
                post,
                null
        );

        this.childPostComment = PostComment.of(
                "testChildId1",
                "testChildContent1",
                writer,
                post,
                postComment
        );

        this.childPostComment2 = PostComment.of(
                "testChildId2",
                "testChildContent2",
                writer,
                post,
                postComment
        );

        this.postComment2 = PostComment.of(
                "testCommentId2",
                "testContent2",
                writer,
                post,
                null
        );

        this.childPostComment3 = PostComment.of(
                "testChildId3",
                "testChildContent3",
                writer,
                post,
                postComment2
        );
    }

    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testContent1", null, "testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

            mockedPostComment.when(() -> PostComment.of("testContent1", this.writer, this.post, null)).thenReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", commentRequestDto);

            // then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("testContent1");
        }
    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            // given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testChildContent1", postComment.getId(), "testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

            mockedPostComment.when(() -> PostComment.of("testChildContent1", this.writer, this.post, postComment)).thenReturn(this.childPostComment);

            given(this.postCommentRepository.save(this.childPostComment)).willReturn(this.childPostComment);

            // when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", commentRequestDto);

            // then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("testChildContent1");
        }
    }

    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        // given
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        ArrayList<PostComment> parents = new ArrayList<>(Arrays.asList(postComment, postComment2));
        ArrayList<PostComment> children1 = new ArrayList<>(Arrays.asList(childPostComment, childPostComment2));
        ArrayList<PostComment> children2 = new ArrayList<>(Arrays.asList(childPostComment3));

        given(this.postCommentRepository.findByPostAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAt(post)).willReturn(parents);
        given(this.postCommentRepository.findFirst3ByParentCommentIdAndIsDeletedFalseOrderByCreatedAt(postComment.getId())).willReturn(children1);
        given(this.postCommentRepository.findFirst3ByParentCommentIdAndIsDeletedFalseOrderByCreatedAt(postComment2.getId())).willReturn(children2);

        // when
        List<CommentFindResponseDto> CommentFindResponseDto = this.postCommentService.findCommentsByPost("testPostId");

        // then
        assertThat(CommentFindResponseDto).isNotNull();
        assertThat(CommentFindResponseDto.size()).isEqualTo(parents.size());
        assertThat(CommentFindResponseDto.get(0).getChildren().size()).isEqualTo(children1.size());
        assertThat(CommentFindResponseDto.get(1).getChildren().size()).isEqualTo(children2.size());
        assertThat(CommentFindResponseDto.get(0).getChildren().get(0).getContent()).isEqualTo(childPostComment.getContent());
    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        // given
        ArrayList<PostComment> children = new ArrayList<>(Arrays.asList(childPostComment, childPostComment2));

        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));
        given(this.postCommentRepository.findAllByParentCommentAndIsDeletedFalseOrderByCreatedAt(postComment)).willReturn(children);

        // when
        List<ChildCommentResponseDto> CommentFindResponseDto = this.postCommentService.findAllChildCommentsByParent("testCommentId");

        // then
        assertThat(CommentFindResponseDto).isNotNull();
        assertThat(CommentFindResponseDto.size()).isEqualTo(children.size());
        assertThat(CommentFindResponseDto.get(0).getContent()).isEqualTo(childPostComment.getContent());
    }

    @Test
    @DisplayName("Success case for update comment")
    void successUpdateComment() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent", "testPostId");

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        given(this.postCommentRepository.save(postComment)).willReturn(postComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.updateComment("testUserId", "testCommentId", commentUpdateRequestDto);

        // then
        assertThat(commentResponseDto).isNotNull();
        assertThat(commentResponseDto.getContent()).isEqualTo("updateContent");
    }

    @Test
    @DisplayName("Update comment by other user")
    void FailUpdateComment() {
        // given
        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("updateContent", "testPostId");

        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(writer2));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        assertThatThrownBy(() -> this.postCommentService.updateComment("testUserId2", "testCommentId", commentUpdateRequestDto))
                // then
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("Success case for delete comment")
    void successDeleteComment() {
        // given
        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
        given(this.postCommentRepository.findById("testChildId1")).willReturn(Optional.of(childPostComment));

        given(this.postCommentRepository.save(childPostComment)).willReturn(childPostComment);

        // when
        CommentResponseDto commentResponseDto = this.postCommentService.deleteComment("testChildId1", "testUserId");

        // then
        assertThat(commentResponseDto).isNotNull();
        assertThat(commentResponseDto.getIsDeleted()).isEqualTo(true);
    }

    @Test
    @DisplayName("Delete comment by other user")
    void FailDeleteComment() {
        // given
        given(this.userRepository.findById("testUserId2")).willReturn(Optional.of(writer2));
        given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

        // when
        assertThatThrownBy(() -> this.postCommentService.deleteComment("testCommentId", "testUserId2"))
                //then
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

}
