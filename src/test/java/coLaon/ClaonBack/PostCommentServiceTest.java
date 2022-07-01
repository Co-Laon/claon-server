package coLaon.ClaonBack;

import coLaon.ClaonBack.post.Service.PostCommentService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
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

import java.util.Optional;
import java.util.Set;

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

    private PostComment postComment;
    private User writer;
    private Post post;
    private PostComment childPostComment;

    @BeforeEach
    void setUp() {
        this.writer = User.of(
                "testUserId",
                "01012341234",
                "test@gmail.com",
                "test1234!!",
                "test",
                "경기도",
                "성남시",
                "",
                "instagramId"
        );

        this.post = Post.of(
                "testPostId",
                "center1",
                "hold",
                "testContent",
                writer,
                Set.of()
        );

        this.postComment = PostComment.of(
                "testCommentId",
                "testContent1",
                writer,
                post,
                null
        );

        this.childPostComment = PostComment.of(
                "testChildContent1",
                writer,
                post,
                postComment
        );
    }


    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            //given
            CommentRequestDto commentRequestDto = new CommentRequestDto("testContent1", null, "testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

            given(PostComment.of("testContent1", this.writer, this.post, null)).willReturn(this.postComment);

            given(this.postCommentRepository.save(this.postComment)).willReturn(this.postComment);
            //when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", commentRequestDto);
            //then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("testContent1");
        }
    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            //given
            CommentRequestDto commentRequestDto = new CommentRequestDto("testChildContent1", postComment.getId(), "testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

            given(PostComment.of("testChildContent1", this.writer, this.post, postComment)).willReturn(this.childPostComment);

            given(this.postCommentRepository.save(this.childPostComment)).willReturn(this.childPostComment);
            //when
            CommentResponseDto commentResponseDto = this.postCommentService.createComment("testUserId", commentRequestDto);
            //then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("testChildContent1");
        }
    }
}
