package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.config.QueryDslTestConfig;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepositorySupport;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
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
