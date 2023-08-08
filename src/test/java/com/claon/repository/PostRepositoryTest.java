package com.claon.repository;

import com.claon.center.domain.CenterImg;
import com.claon.center.domain.Charge;
import com.claon.center.domain.ChargeElement;
import com.claon.center.domain.OperatingTime;
import com.claon.center.repository.HoldInfoRepository;
import com.claon.config.QueryDslTestConfig;
import com.claon.user.domain.User;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.center.domain.Center;
import com.claon.center.domain.HoldInfo;
import com.claon.center.repository.CenterRepository;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.repository.ClimbingHistoryRepository;
import com.claon.post.repository.PostRepository;
import com.claon.post.repository.PostRepositorySupport;
import com.claon.user.domain.BlockUser;
import com.claon.user.domain.Laon;
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
    private UserRepository userRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private LaonRepository laonRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostRepositorySupport postRepositorySupport;
    @Autowired
    private HoldInfoRepository holdInfoRepository;
    @Autowired
    private ClimbingHistoryRepository climbingHistoryRepository;

    private User user;
    private Post deletedPost;
    private Center center;
    private HoldInfo holdInfo;

    @BeforeEach
    void setUp() {
        // given
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

        userRepository.save(User.of(
                "block@gmail.com",
                "1264567890",
                "testBlockNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        ));

        User blockUser = userRepository.save(User.of(
                "private@gmail.com",
                "1264567890",
                "testPrivateNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        ));

        User user2 = userRepository.save(User.of(
                "another@gmail.com",
                "1264567890",
                "testNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramIdAnother"
        ));

        User user3 = userRepository.save(User.of(
                "another2@gmail.com",
                "1264567890",
                "testNickname2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramIdAnother2"
        ));

        blockUserRepository.save(BlockUser.of(
                this.user,
                blockUser
        ));

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
                "hold info img test"
        );
        this.center = centerRepository.save(center);

        Post post = postRepository.save(Post.of(
                center,
                "testContent",
                user,
                List.of(),
                List.of()
        ));

       postRepository.save(Post.of(
                center,
                "testContent",
                user2,
                List.of(),
                List.of()
        ));

        postRepository.save(Post.of(
                center,
                "testContent",
                user3,
                List.of(),
                List.of()
        ));

        laonRepository.save(Laon.of(user, user3));

        Post blockedPost = postRepository.save(Post.of(
                center,
                "testContent",
                blockUser,
                List.of(),
                List.of()
        ));

        Post privatePost = postRepository.save(Post.of(
                center,
                "testContent",
                blockUser,
                List.of(),
                List.of()
        ));

        this.deletedPost = Post.of(
                center,
                "testContent",
                user,
                List.of(),
                List.of()
        );
        this.deletedPost.delete();
        postRepository.save(this.deletedPost);

        this.holdInfo = holdInfoRepository.save(HoldInfo.of("hold1", "test", center));

        climbingHistoryRepository.save(ClimbingHistory.of(post, holdInfo, 1));
        climbingHistoryRepository.save(ClimbingHistory.of(blockedPost, holdInfo, 1));
        climbingHistoryRepository.save(ClimbingHistory.of(privatePost, holdInfo, 1));
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
        // given
        String userId = user.getId();

        // when
        List<String> postIds = postRepository.selectPostIdsByUserId(userId);

        // then
        assertThat(postIds.size()).isEqualTo(1);
    }

    @Test
    public void successFindByWriterAndIsDeletedFalse() {
        // when
        Page<Post> postList = postRepository.findByWriterAndIsDeletedFalse(user, PageRequest.of(0, 2));

        // then
        assertThat(postList.getContent().size()).isEqualTo(1);
    }

    @Test
    public void findExceptLaonUserAndBlockUser() {
        // given
        String userId = user.getId();

        // when
        Page<Post> results = postRepositorySupport.findExceptLaonUserAndBlockUser(userId, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void findLaonUserPostsExceptBlockUser() {
        // given
        String userId = user.getId();

        // when
        Page<Post> results = postRepositorySupport.findLaonUserPostsExceptBlockUser(userId, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindByCenterExceptBlockUser() {
        // given
        String centerId = center.getId();
        String userId = user.getId();

        // when
        Page<Post> results = postRepositorySupport.findByCenterExceptBlockUser(centerId, userId, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(2);
    }

    @Test
    public void successFindByCenterAndHoldExceptBlockUser() {
        // given
        String centerId = center.getId();
        String userId = user.getId();
        String holdInfoId = holdInfo.getId();

        // when
        Page<Post> results = postRepositorySupport.findByCenterAndHoldExceptBlockUser(centerId, holdInfoId, userId, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successFindByNicknameAndCenterAndYearMonth() {
        // given
        String centerId = center.getId();
        String userId = user.getId();
        LocalDateTime now = LocalDateTime.now();

        // when
        Page<Post> results = postRepositorySupport.findByNicknameAndCenterAndYearMonth(userId, "testNickname", centerId, now.getYear(), now.getMonthValue(), PageRequest.of(0, 3));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }

    @Test
    public void successCountByCenterExceptBlockUser() {
        // given
        String centerId = center.getId();
        String userId = user.getId();

        // when
        Integer countPost = postRepositorySupport.countByCenterExceptBlockUser(centerId, userId);

        // then
        assertThat(countPost).isEqualTo(3);
    }

    @Test
    public void successFindByCenterIdAndUserId() {
        // given
        String centerId = center.getId();
        String userId = user.getId();

        // when
        List<Post> results = postRepositorySupport.findByCenterIdAndUserId(centerId, userId);

        // then
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void successFindCenterByUser() {
        // given
        String userId = user.getId();

        // when
        Page<Center> results = postRepositorySupport.findCenterByUser(userId, PageRequest.of(0, 2));

        // then
        assertThat(results.getContent().size()).isEqualTo(1);
    }
}
