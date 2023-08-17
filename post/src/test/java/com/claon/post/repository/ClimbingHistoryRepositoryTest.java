package com.claon.post.repository;

import com.claon.post.config.QueryDslTestConfig;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslTestConfig.class, ClimbingHistoryRepositorySupport.class})
public class ClimbingHistoryRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;
    @Autowired
    private ClimbingHistoryRepositorySupport climbingHistoryRepositorySupport;

    private final String USER_ID = "USER_ID";
    private final String HOLD_ID = "HOLD_ID";
    private final String CENTER_ID = "CENTER_ID";
    private Post post;
    private ClimbingHistory climbingHistory;

    @BeforeEach
    void setUp() {
        this.post = postRepository.save(Post.of(
                CENTER_ID,
                "testContent1",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                USER_ID
        ));

        this.climbingHistory = climbingHistoryRepository.save(ClimbingHistory.of(this.post, HOLD_ID, 1));
    }

    @Test
    public void successFindByPostIds() {
        // given
        List<String> postIdList = List.of(this.post.getId());

        // when
        List<ClimbingHistory> histories = climbingHistoryRepository.findByPostIds(postIdList);

        // then
        assertThat(histories.size()).isEqualTo(1);
    }

    @Test
    public void successDeleteAllByPost() {
        // given
        String postId = this.post.getId();

        // when
        climbingHistoryRepository.deleteAllByPost(postId);

        // then
        assertTrue(climbingHistoryRepository.findById(climbingHistory.getId()).isEmpty());
    }

    @Test
    public void successFindHistoryByDate() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        List<ClimbingHistory> histories = climbingHistoryRepositorySupport.findHistoryByDate(USER_ID, now.getYear(), now.getMonthValue());

        // then
        assertThat(histories.size()).isEqualTo(1);
    }
}
