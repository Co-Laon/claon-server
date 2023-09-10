package com.claon.post.repository;

import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ClimbingHistoryRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;

    private Post post;
    private ClimbingHistory climbingHistory;

    @BeforeEach
    void setUp() {
        post = postRepository.save(Post.of(
                "CENTER_ID",
                "testContent1",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                "USER_ID"
        ));

        climbingHistory = climbingHistoryRepository.save(ClimbingHistory.of(post, "HOLD_ID", 1));
    }

    @Test
    public void successFindByPostIds() {
        // given
        List<String> postIdList = List.of(post.getId());

        // when
        var histories = climbingHistoryRepository.findByPostIds(postIdList);

        // then
        assertThat(histories.size()).isEqualTo(1);
    }

    @Test
    public void successDeleteAllByPost() {
        // given
        String postId = post.getId();

        // when
        climbingHistoryRepository.deleteAllByPost(postId);

        // then
        assertTrue(climbingHistoryRepository.findById(climbingHistory.getId()).isEmpty());
    }
}
