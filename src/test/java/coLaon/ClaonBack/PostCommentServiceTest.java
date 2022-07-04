package coLaon.ClaonBack;

import coLaon.ClaonBack.post.Service.PostCommentService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
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
                "testChildContent1",
                writer,
                post,
                postComment
        );

        this.childPostComment2 = PostComment.of(
                "testchildId2",
                "testchildContent2",
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
                "testchildId3",
                "testchildContent3",
                writer,
                post,
                postComment2
        );
    }


    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<PostComment> mockedPostComment = mockStatic(PostComment.class)) {
            //given
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testContent1", null, "testPostId");

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
            CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("testChildContent1", postComment.getId(), "testPostId");

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
    @Test
    @DisplayName("Success case for find parent comments")
    void successFindParentComments() {
        try (MockedStatic<PostComment> mockedLaonComment = mockStatic(PostComment.class)) {
            //given
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

            ArrayList<PostComment> parents = new ArrayList<PostComment>(Arrays.asList(postComment, postComment2));
            ArrayList<PostComment> children1 = new ArrayList<PostComment>(Arrays.asList(childPostComment, childPostComment2));
            ArrayList<PostComment> children2 = new ArrayList<PostComment>(Arrays.asList(childPostComment3));

            given(this.postCommentRepository.findByPostAndParentCommentIsNullOrderByCreatedAt(post)).willReturn(parents);
            given(this.postCommentRepository.findFirst3ByParentCommentIdOrderByCreatedAt(postComment.getId())).willReturn(children1);
            given(this.postCommentRepository.findFirst3ByParentCommentIdOrderByCreatedAt(postComment2.getId())).willReturn(children2);
            //when
            List<CommentFindResponseDto> CommentFindResponseDto = this.postCommentService.findCommentsByPost("testPostId");
            //then
            assertThat(CommentFindResponseDto).isNotNull();
            assertThat(CommentFindResponseDto.size()).isEqualTo(parents.size());
            assertThat(CommentFindResponseDto.get(0).getChildren().size()).isEqualTo(children1.size());
            assertThat(CommentFindResponseDto.get(1).getChildren().size()).isEqualTo(children2.size());
            assertThat(CommentFindResponseDto.get(0).getChildren().get(0).getContent()).isEqualTo(childPostComment.getContent());
        }
    }

    @Test
    @DisplayName("Success case for find child comments")
    void successFindChildComments() {
        try (MockedStatic<PostComment> mockedLaonComment = mockStatic(PostComment.class)) {
            //given
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));
            ArrayList<PostComment> children = new ArrayList<PostComment>(Arrays.asList(childPostComment, childPostComment2));
            given(this.postCommentRepository.findAllByParentCommentOrderByCreatedAt(postComment)).willReturn(children);
            //when
            List<CommentFindResponseDto> CommentFindResponseDto = this.postCommentService.findAllChildCommentsByParent("testCommentId");
            //then
            assertThat(CommentFindResponseDto).isNotNull();
            assertThat(CommentFindResponseDto.size()).isEqualTo(children.size());
            assertThat(CommentFindResponseDto.get(0).getContent()).isEqualTo(childPostComment.getContent());
        }
    }

    @Test
    @DisplayName("Success case for update comment")
    void successUpdateComment() {
        try (MockedStatic<PostComment> mockedLaonComment = mockStatic(PostComment.class)) {
            //given
            CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("testCommentId","updateContent","testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postCommentRepository.findById("testCommentId")).willReturn(Optional.of(postComment));

            given(this.postCommentRepository.save(postComment)).willReturn(postComment);
            //when
            CommentResponseDto commentResponseDto = this.postCommentService.updateComment("testUserId", commentUpdateRequestDto);
            //then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("updateContent");
        }
    }

    @Test
    @DisplayName("Success case for delete comment")
    void successDeleteComment() {
        try (MockedStatic<PostComment> mockedLaonComment = mockStatic(PostComment.class)) {
            //given
            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.postCommentRepository.findById("testchildId1")).willReturn(Optional.of(childPostComment));

            given(this.postCommentRepository.save(childPostComment)).willReturn(childPostComment);
            //when
            CommentResponseDto commentResponseDto = this.postCommentService.deleteComment("testchildId1", "testUserId");
            //then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getIsDeleted()).isEqualTo(true);
        }
    }
}
