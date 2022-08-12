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
import coLaon.ClaonBack.config.QueryDslTestConfig;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslTestConfig.class, PostRepositorySupport.class})
public class PostRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostRepositorySupport postRepositorySupport;
    @Autowired
    private HoldInfoRepository holdInfoRepository;
    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;

    private User user;
    private Post post;
    private Center center;
    private HoldInfo holdInfo;
    private ClimbingHistory climbingHistory;

    @BeforeEach
    void setUp(){
        // given
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
        userRepository.save(user);
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
        this.center = centerRepository.save(center);
        this.post = Post.of(
                center,
                "testContent1",
                user,
                List.of(),
                Set.of()
        );
        postRepository.save(post);
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Post post = Post.of(this.center, "content" + i, user, List.of(), Set.of());
            posts.add(post);
        }
        postRepository.saveAll(posts);
        this.holdInfo = HoldInfo.of("hold1", "test", center);
        holdInfoRepository.save(holdInfo);
        this.climbingHistory = ClimbingHistory.of(post, holdInfo, 1);
        climbingHistoryRepository.save(climbingHistory);
    }

    @Test
    public void checkSelectPostIdsByUserId(){
        // when
        List<String> postIds = postRepository.selectPostIdsByUserId(user.getId());

        // then
        assertThat(postIds.contains(post.getId())).isTrue();
    }

    @Test
    public void checkFindByUserIdOrderByCreatedAtDesc(){
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Post> results = postRepository.findByWriterOrderByCreatedAtDesc(this.user, pageable);
        assertThat(results.getTotalElements()).isEqualTo(6L);
    }

    @Test
    public void testFindByCenterExceptBlockUser() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Post> results = postRepositorySupport.findByCenterExceptBlockUser(center.getId(),user.getId(), pageable);
        assertThat(results.getTotalElements()).isEqualTo(6L);
    }

    @Test
    public void testFindByCenterAndHoldExceptBlockUser() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Post> results = postRepositorySupport.findByCenterAndHoldExceptBlockUser(center.getId(), holdInfo.getId(), user.getId(), pageable);
        assertThat(results.getTotalElements()).isEqualTo(1L);
    }
}
