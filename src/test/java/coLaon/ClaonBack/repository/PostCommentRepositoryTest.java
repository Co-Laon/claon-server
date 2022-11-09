package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.config.QueryDslTestConfig;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostCommentRepositorySupport;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QueryDslTestConfig.class, PostCommentRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PostCommentRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private PostCommentRepositorySupport postCommentRepositorySupport;

    private User user, blockUser;
    private Post post;
    private PostComment deletedComment;

    @BeforeEach
    void setUp() {
        this.user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        ));

        this.blockUser = userRepository.save(User.of(
                "block@gmail.com",
                "1264567890",
                "testBlockNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        ));

        blockUserRepository.save(BlockUser.of(
                this.user,
                this.blockUser
        ));

        Center center = centerRepository.save(Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        ));

        this.post = postRepository.save(Post.of(
                center,
                "testContent1",
                user,
                List.of(),
                List.of()
        ));

        this.deletedComment = PostComment.of(
                "testContent1",
                user,
                post,
                null
        );
        this.deletedComment.delete();
        this.postCommentRepository.save(this.deletedComment);
    }

    @Test
    public void successFindByIdAndIsDeletedFalse() {
        // given
        String commentId = deletedComment.getId();

        // when
        Optional<PostComment> postCommentOptional = postCommentRepository.findByIdAndIsDeletedFalse(commentId);

        // then
        assertThat(postCommentOptional).isNotPresent();
    }

    @Test
    public void successFindParentCommentByPost() {
        // given
        String postId = post.getId();
        String userId = user.getId();

        this.postCommentRepository.save(PostComment.of(
                "testContent1",
                user,
                post,
                null
        ));
        this.postCommentRepository.save(PostComment.of(
                "testContent2",
                blockUser,
                post,
                null
        ));

        // when
        Page<PostComment> commentList = postCommentRepositorySupport.findParentCommentByPost(postId, userId, PageRequest.of(0, 2));

        // then
        assertThat(commentList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindChildCommentByParentComment() {
        // given
        PostComment parentComment = PostComment.of(
                "testContent1",
                user,
                post,
                null
        );
        this.postCommentRepository.save(parentComment);

        this.postCommentRepository.save(PostComment.of(
                "testChildContent1",
                user,
                post,
                parentComment
        ));
        this.postCommentRepository.save(PostComment.of(
                "testChildContent2",
                blockUser,
                post,
                parentComment
        ));

        String parentCommentId = parentComment.getId();
        String userId = user.getId();

        // when
        Page<PostComment> commentList = postCommentRepositorySupport.findChildCommentByParentComment(parentCommentId, userId, PageRequest.of(0, 2));

        // then
        assertThat(commentList.getContent().size()).isEqualTo(1);
    }
}
