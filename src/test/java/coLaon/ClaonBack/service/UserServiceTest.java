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
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.dto.BlockUserFindResponseDto;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.IndividualUserResponseDto;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

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
    BlockUserRepository blockUserRepository;
    @Mock
    ClimbingHistoryRepository climbingHistoryRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    UserService userService;

    private User user, privateUser, publicUser, blockUser;
    private Center center;
    private BlockUser blockUserRelation;
    private Laon laonRelation;
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

        this.blockUser = User.of(
                "blockUserId",
                "block@gmail.com",
                "1264567890",
                "testBlockNickname",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId2"
        );

        this.blockUserRelation = BlockUser.of(
                this.publicUser,
                this.blockUser
        );

        this.laonRelation = Laon.of(
                this.blockUser,
                this.publicUser
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

    @Test
    @DisplayName("Success case for block user")
    void successBlockUser() {
        try (MockedStatic<BlockUser> mockedBlock = mockStatic(BlockUser.class)) {
            // given
            given(this.userRepository.findByNickname("testBlockNickname")).willReturn(Optional.of(blockUser));
            given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));
            given(this.blockUserRepository.findByUserIdAndBlockId(this.publicUser.getId(), this.blockUser.getId())).willReturn(Optional.empty());
            given(this.laonRepository.findByLaonIdAndUserId("blockUserId", "publicUserId")).willReturn(Optional.of(this.laonRelation));

            mockedBlock.when(() -> BlockUser.of(this.publicUser, this.blockUser)).thenReturn(this.blockUserRelation);

            given(this.blockUserRepository.save(this.blockUserRelation)).willReturn(this.blockUserRelation);

            // when
            this.userService.createBlock("publicUserId", "testBlockNickname");

            // then
            assertThat(this.blockUserRepository.findByUserIdAndBlockId(this.publicUser.getId(), this.blockUser.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Success case for unblock user")
    void successUnblockUser() {
        // given
        given(this.userRepository.findByNickname("testBlockNickname")).willReturn(Optional.of(blockUser));
        given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));
        given(this.blockUserRepository.findByUserIdAndBlockId(this.publicUser.getId(), this.blockUser.getId())).willReturn(Optional.of(blockUserRelation));

        // when
        this.userService.deleteBlock("publicUserId", "testBlockNickname");

        // then
        assertThat(this.blockUserRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Success case for find block users")
    void successFindBlockUsers() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));

        Page<BlockUser> blockUsers = new PageImpl<>(List.of(blockUserRelation), pageable, 2);
        given(this.blockUserRepository.findByUserId(publicUser.getId(), pageable)).willReturn(blockUsers);

        // when
        Pagination<BlockUserFindResponseDto> blockUserFindResponseDto = this.userService.findBlockUser("publicUserId", pageable);

        // then
        assertThat(blockUserFindResponseDto.getResults())
                .isNotNull()
                .extracting(
                        BlockUserFindResponseDto::getBlockUserNickName,
                        BlockUserFindResponseDto::getBlockUserProfileImage)
                .containsExactly(
                        tuple("testBlockNickname", "")
                );
    }
}
