package com.claon.post.repository;

import com.claon.post.config.QueryDslTestConfig;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslTestConfig.class, PostRepositorySupport.class})
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostRepositorySupport postRepositorySupport;
    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;

    private final String USER_ID = "USER_ID";
    private final String USER2_ID = "USER2_ID";
    private final String USER3_ID = "USER3_ID";
    private final String HOLD_ID = "HOLD_ID";
    private final String CENTER_ID = "CENTER_ID";
    private Post deletedPost;

    @BeforeEach
    void setUp() {
        Post post = postRepository.save(Post.of(
                CENTER_ID,
                "testContent",
                USER_ID,
                List.of(),
                List.of()
        ));

       postRepository.save(Post.of(
                CENTER_ID,
                "testContent",
                USER2_ID,
                List.of(),
                List.of()
        ));

        postRepository.save(Post.of(
                CENTER_ID,
                "testContent",
                USER3_ID,
                List.of(),
                List.of()
        ));

//        Post blockedPost = postRepository.save(Post.of(
//                center,
//                "testContent",
//                blockUser,
//                List.of(),
//                List.of()
//        ));
//
//        Post privatePost = postRepository.save(Post.of(
//                center,
//                "testContent",
//                blockUser,
//                List.of(),
//                List.of()
//        ));

        this.deletedPost = Post.of(
                CENTER_ID,
                "testContent",
                USER_ID,
                List.of(),
                List.of()
        );
        this.deletedPost.delete();
        postRepository.save(this.deletedPost);

        climbingHistoryRepository.save(ClimbingHistory.of(post, HOLD_ID, 1));
//        climbingHistoryRepository.save(ClimbingHistory.of(blockedPost, HOLD_ID, 1));
//        climbingHistoryRepository.save(ClimbingHistory.of(privatePost, HOLD_ID, 1));
    }

    @Test
    public void successFindByIdAndIsDeletedFalse() {
        // given
        String postId = this.deletedPost.getId();

        // when
        Optional<Post> postOptional = postRepository.findByIdAndIsDeletedFalse(postId);

        // then
        assertThat(postOptional).isNotPresent();
    }

    @Test
    public void successSelectPostIdsByUserId() {
        // when
        List<String> postIds = postRepository.selectPostIdsByUserId(USER_ID);

        // then
        assertThat(postIds.size()).isEqualTo(1);
    }

    @Test
    public void successFindByWriterAndIsDeletedFalse() {
        // when
        Page<Post> postList = postRepository.findByWriterAndIsDeletedFalse(USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(postList.getContent().size()).isEqualTo(1);
    }

//    @Test
//    public void findExceptLaonUserAndBlockUser() {
//        // when
//        Page<Post> results = postRepositorySupport.findExceptLaonUserAndBlockUser(USER_ID, PageRequest.of(0, 2));
//
//        // then
//        assertThat(results.getContent().size()).isEqualTo(1);
//    }

//    @Test
//    public void findLaonUserPostsExceptBlockUser() {
//        // when
//        Page<Post> results = postRepositorySupport.findLaonUserPostsExceptBlockUser(USER_ID, PageRequest.of(0, 2));
//
//        // then
//        assertThat(results.getContent().size()).isEqualTo(1);
//    }

    @Test
    public void successFindByCenterExceptBlockUser() {
        // when
        Page<Post> results = postRepositorySupport.findByCenterExceptBlockUser(CENTER_ID, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(2);
    }

    @Test
    public void successFindByCenterAndHoldExceptBlockUser() {
        // when
        Page<Post> results = postRepositorySupport.findByCenterAndHoldExceptBlockUser(CENTER_ID, HOLD_ID, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

//    @Test
//    public void successFindByNicknameAndCenterAndYearMonth() {
//        // given
//        LocalDateTime now = LocalDateTime.now();
//
//        // when
//        Page<Post> results = postRepositorySupport.findByNicknameAndCenterAndYearMonth(USER_ID, "testNickname", CENTER_ID, now.getYear(), now.getMonthValue(), PageRequest.of(0, 3));
//
//        // then
//        assertThat(results.getContent().size()).isEqualTo(1);
//    }

    @Test
    public void successCountByCenterExceptBlockUser() {
        // when
        Integer countPost = postRepositorySupport.countByCenterExceptBlockUser(CENTER_ID, USER_ID);

        // then
        assertThat(countPost).isEqualTo(3);
    }

    @Test
    public void successFindByCenterIdAndUserId() {
        // when
        List<Post> results = postRepositorySupport.findByCenterIdAndUserId(CENTER_ID, USER_ID);

        // then
        assertThat(results.size()).isEqualTo(1);
    }

//    @Test
//    public void successFindCenterByUser() {
//        // given
//        String userId = user.getId();
//
//        // when
//        Page<Center> results = postRepositorySupport.findCenterByUser(userId, PageRequest.of(0, 2));
//
//        // then
//        assertThat(results.getContent().size()).isEqualTo(1);
//    }
}
