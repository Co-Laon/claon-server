package coLaon.ClaonBack.repository;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private CenterRepository centerRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;

    @Autowired
    private HoldInfoRepository holdInfoRepository;

    private User user;
    private Post post;
    private ClimbingHistory climbingHistory;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );
        user = userRepository.save(user);

        Center center = Center.of(
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
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );
        center = centerRepository.save(center);

        // Setting climbing History
        HoldInfo holdInfo = HoldInfo.of("name", "dfdf", center);
        holdInfo = holdInfoRepository.save(holdInfo);

        this.post = Post.of(
                center,
                "testContent1",
                user
        );
        post = postRepository.save(post);

        climbingHistory = ClimbingHistory.of(this.post, holdInfo, 1);
        climbingHistory = climbingHistoryRepository.save(climbingHistory);
    }

    @Test
    public void successFindByPostIds() {
        // when
        List<ClimbingHistory> histories = climbingHistoryRepository.findByPostIds(List.of(this.post.getId()));

        // then
        assertThat(histories.size()).isEqualTo(1);
    }

    @Test
    public void successDeleteAllByPost() {
        // when
        climbingHistoryRepository.deleteAllByPost(this.post.getId());

        // then
        assertTrue(climbingHistoryRepository.findById(climbingHistory.getId()).isEmpty());
    }
}
