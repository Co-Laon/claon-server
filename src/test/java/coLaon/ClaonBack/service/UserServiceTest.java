package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.IndividualUserResponseDto;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    LaonRepository laonRepository;
    @Mock
    ClimbingHistoryRepository climbingHistoryRepository;

    @InjectMocks
    UserService userService;

    private User user, privateUser, publicUser;
    private Center center;
    private Post post;
    private HoldInfo holdInfo;
    private ClimbingHistory climbingHistory;
    private List<String> postIds;

    @BeforeEach
    void setUp() {
        this.publicUser = User.of(
                "publicUserId",
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.privateUser = User.of(
                "privateUserId",
                "test12@gmail.com",
                "1234567823",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );
        this.privateUser.changePublicScope();

        this.user = User.of(
                "userId",
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.center = Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge("자유 패키지", "330,000")),
                "charge img test",
                "hold info img test",
                List.of(new SectorInfo("test sector", "1/1", "1/2"))
        );

        this.post = Post.of(
                "testPostId",
                center,
                "testContent1",
                user,
                Set.of(),
                Set.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.postIds = List.of(this.post.getId());
        this.holdInfo = HoldInfo.of("test", "name", "dfdf", center);
        this.climbingHistory = ClimbingHistory.of(
                this.post,
                holdInfo,
                2);
    }

    @Test
    @DisplayName("Success case for retrieving me")
    void successRetrieveMe() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));

        // when
        UserResponseDto userResponseDto = this.userService.getUser("userId");

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("email", "instagramUserName", "isPrivate")
                .contains("test@gmail.com", "instagramId", false);
    }

    @Test
    @DisplayName("Success case for modifying single user")
    void successModifyUser() {
        // given
        UserModifyRequestDto dto = new UserModifyRequestDto(
                "nickname",
                "경기도",
                "성남시",
                "",
                "hoonki",
                "dfdf"
        );

        given(this.userRepository.findById("userId")).willReturn(Optional.of(this.user));
        given(this.userRepository.save(this.user)).willReturn(this.user);

        // when
        UserResponseDto userResponseDto = this.userService.modifyUser("userId", dto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("email", "nickname")
                .contains("test@gmail.com", "nickname");
    }

    @Test
    @DisplayName("Success case for retrieving single other user")
    void successRetrieveUser() {
        // given
        given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(user));
        given(this.postRepository.selectPostIdsByUserId("userId")).willReturn(postIds);
        given(this.postLikeRepository.countByPostIdIn(postIds)).willReturn(5L);
        given(this.laonRepository.getUserIdsByLaonId("userId")).willReturn(Set.of("publicUserId"));
        given(this.climbingHistoryRepository.findByPostIds(this.postIds)).willReturn(List.of(this.climbingHistory));

        // when
        IndividualUserResponseDto userResponseDto = this.userService.getOtherUserInformation("publicUserId", "userNickname");

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting(
                        IndividualUserResponseDto::getMetropolitanActiveArea,
                        IndividualUserResponseDto::getPostCount,
                        IndividualUserResponseDto::getLikeCount,
                        IndividualUserResponseDto::getLaonCount,
                        IndividualUserResponseDto::getIsLaon)
                .contains("경기도", 1L, 5L, 1L, true);

        assertThat(userResponseDto.getCenterClimbingHistories())
                .isNotNull()
                .extracting(
                        CenterClimbingHistoryResponseDto::getCenterName,
                        history -> history.getClimbingHistories().get(0).getClimbingCount())
                .containsExactly(
                        tuple(center.getName(), 2)
                );
    }

    @Test
    @DisplayName("Success case for retrieving single other private user")
    void successRetrievePrivateUser() {
        // given
        given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(privateUser));
        given(this.postRepository.selectPostIdsByUserId("privateUserId")).willReturn(postIds);
        given(this.postLikeRepository.countByPostIdIn(postIds)).willReturn(5L);
        given(this.laonRepository.getUserIdsByLaonId("privateUserId")).willReturn(new HashSet<>());

        // when
        IndividualUserResponseDto userResponseDto = this.userService.getOtherUserInformation("publicUserId", "userNickname");

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting(
                        IndividualUserResponseDto::getMetropolitanActiveArea,
                        IndividualUserResponseDto::getBasicLocalActiveArea,
                        IndividualUserResponseDto::getLaonCount,
                        IndividualUserResponseDto::getIsLaon)
                .contains(null, null, 0L, false);
    }

    @Test
    @DisplayName("Success case for set public user private account")
    void successSetPrivateAccount() {
        // given
        given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));
        given(this.userRepository.save(publicUser)).willReturn(publicUser);

        // when
        PublicScopeResponseDto publicScopeResponseDto = this.userService.changePublicScope("publicUserId");

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isTrue();
    }

    @Test
    @DisplayName("Success case for set private user public account")
    void successSetPublicAccount() {
        // given
        given(this.userRepository.findById("privateUserId")).willReturn(Optional.of(privateUser));
        given(this.userRepository.save(privateUser)).willReturn(privateUser);

        // when
        PublicScopeResponseDto publicScopeResponseDto = this.userService.changePublicScope("privateUserId");

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isFalse();
    }
}
