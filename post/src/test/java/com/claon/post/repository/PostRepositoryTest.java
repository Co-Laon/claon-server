package com.claon.post.repository;

import com.claon.post.config.QueryDslTestConfig;
import com.claon.post.domain.BlockUser;
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
    @Autowired
    private BlockUserRepository blockUserRepository;

    private final String USER_ID = "USER_ID";
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

        BlockUser blockUser = blockUserRepository.save(BlockUser.of(
                USER_ID,
                "BLOCKED_ID"
        ));

        Post blockedPost = postRepository.save(Post.of(
                CENTER_ID,
                "testContent",
                blockUser.getBlockedUserId(),
                List.of(),
                List.of()
        ));

        deletedPost = Post.of(
                CENTER_ID,
                "testContent",
                USER_ID,
                List.of(),
                List.of()
        );
        deletedPost.delete();
        postRepository.save(deletedPost);

        climbingHistoryRepository.save(ClimbingHistory.of(post, HOLD_ID, 1));
        climbingHistoryRepository.save(ClimbingHistory.of(blockedPost, HOLD_ID, 1));
    }

    @Test
    public void successFindByIdAndIsDeletedFalse() {
        // given
        String postId = deletedPost.getId();

        // when
        var postOptional = postRepository.findByIdAndIsDeletedFalse(postId);

        // then
        assertThat(postOptional).isNotPresent();
    }

    @Test
    public void successSelectPostIdsByUserId() {
        // when
        var postIds = postRepository.selectPostIdsByUserId(USER_ID);

        // then
        assertThat(postIds.size()).isEqualTo(1);
    }

    @Test
    public void successFindByWriterAndIsDeletedFalse() {
        // when
        var postList = postRepository.findByWriterAndIsDeletedFalse(USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(postList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void findExceptBlockUser() {
        // when
        Page<Post> results = postRepositorySupport.findExceptBlockUser(USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(0);
    }

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
        var results = postRepositorySupport.findByCenterExceptBlockUser(CENTER_ID, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindByCenterAndHoldExceptBlockUser() {
        // when
        var results = postRepositorySupport.findByCenterAndHoldExceptBlockUser(CENTER_ID, HOLD_ID, USER_ID, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindByCenterAndYearMonth() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        Page<Post> results = postRepositorySupport.findByCenterAndYearMonth(USER_ID, CENTER_ID, now.getYear(), now.getMonthValue(), PageRequest.of(0, 3));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

//    @Test
//    public void successFindCenterByUser() {
//        // when
//        Page<Center> results = postRepositorySupport.findCenterByUser(userId, PageRequest.of(0, 2));
//
//        // then
//        assertThat(results.getContent().size()).isEqualTo(1);
//    }
}
