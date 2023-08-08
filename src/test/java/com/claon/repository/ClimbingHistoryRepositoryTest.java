package com.claon.repository;

import com.claon.center.domain.CenterImg;
import com.claon.center.domain.Charge;
import com.claon.center.domain.ChargeElement;
import com.claon.center.domain.OperatingTime;
import com.claon.center.repository.HoldInfoRepository;
import com.claon.config.QueryDslTestConfig;
import com.claon.user.domain.User;
import com.claon.user.repository.UserRepository;
import com.claon.center.domain.Center;
import com.claon.center.domain.HoldInfo;
import com.claon.center.repository.CenterRepository;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import com.claon.post.repository.ClimbingHistoryRepository;
import com.claon.post.repository.ClimbingHistoryRepositorySupport;
import com.claon.post.repository.PostRepository;
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
    private UserRepository userRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;
    @Autowired
    private ClimbingHistoryRepositorySupport climbingHistoryRepositorySupport;
    @Autowired
    private HoldInfoRepository holdInfoRepository;

    private User user;
    private Post post;
    private ClimbingHistory climbingHistory;

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

        HoldInfo holdInfo = holdInfoRepository.save(HoldInfo.of("name", "dfdf", center));

        this.post = postRepository.save(Post.of(
                center,
                "testContent1",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                user
        ));

        this.climbingHistory = climbingHistoryRepository.save(ClimbingHistory.of(this.post, holdInfo, 1));
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
        String userId = user.getId();

        // when
        List<ClimbingHistory> histories = climbingHistoryRepositorySupport.findHistoryByDate(userId, now.getYear(), now.getMonthValue());

        // then
        assertThat(histories.size()).isEqualTo(1);
    }
}
