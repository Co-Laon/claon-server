package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.user.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HistoryByCenterFindResponseDto;
import coLaon.ClaonBack.user.dto.HistoryGroupByMonthDto;
import coLaon.ClaonBack.user.dto.UserCenterPreviewResponseDto;
import coLaon.ClaonBack.user.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.user.dto.UserDetailResponseDto;
import coLaon.ClaonBack.user.dto.UserPostThumbnailResponseDto;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.IndividualUserResponseDto;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.UserPreviewResponseDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.repository.UserRepositorySupport;
import coLaon.ClaonBack.user.service.CenterPort;
import coLaon.ClaonBack.user.service.PostPort;
import coLaon.ClaonBack.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserRepositorySupport userRepositorySupport;
    @Mock
    LaonRepository laonRepository;
    @Mock
    BlockUserRepository blockUserRepository;
    @Mock
    PostPort postPort;
    @Mock
    CenterPort centerPort;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    UserService userService;

    private User user, privateUser, publicUser;
    private Center center;
    private Post post2, post3;
    private ClimbingHistory climbingHistory, climbingHistory2;
    private List<String> postIds;

    @BeforeEach
    void setUp() {
        this.publicUser = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.publicUser, "id", "publicUserId");

        this.privateUser = User.of(
                "test12@gmail.com",
                "1234567823",
                "private user",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        this.privateUser.changePublicScope();
        ReflectionTestUtils.setField(this.privateUser, "id", "privateUserId");

        this.user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.user, "id", "userId");

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
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        );
        ReflectionTestUtils.setField(center, "id", "centerId");


        HoldInfo holdInfo = HoldInfo.of("test hold", "hold img test", this.center);
        ReflectionTestUtils.setField(holdInfo, "id", "holdId1");

        HoldInfo holdInfo2 = HoldInfo.of("test hold2", "hold img test2", this.center);
        ReflectionTestUtils.setField(holdInfo2, "id", "holdId2");

        Post post = Post.of(
                center,
                "testContent1",
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                user
        );
        ReflectionTestUtils.setField(post, "id", "testPostId");
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());

        this.postIds = List.of(post.getId());

        this.climbingHistory = ClimbingHistory.of(
                post2,
                holdInfo,
                2
        );

        this.climbingHistory2 = ClimbingHistory.of(
                post3,
                holdInfo2,
                1
        );
        ReflectionTestUtils.setField(climbingHistory2, "id", "climbingId2");

        this.post2 = Post.of(
                center,
                "testContent1",
                user,
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                List.of(climbingHistory)
        );
        ReflectionTestUtils.setField(post2, "id", "testPostId2");
        ReflectionTestUtils.setField(post2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post2, "updatedAt", LocalDateTime.now());

        this.post3 = Post.of(
                center,
                "testContent2",
                user,
                List.of(PostContents.of(
                        "test2.com/test.png"
                )),
                List.of(climbingHistory2)
        );
        ReflectionTestUtils.setField(post3, "id", "testPostId3");
        ReflectionTestUtils.setField(post3, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post3, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for set public user private account")
    void successSetPrivateAccount() {
        // given
        given(this.userRepository.save(publicUser)).willReturn(publicUser);

        // when
        PublicScopeResponseDto publicScopeResponseDto = this.userService.changePublicScope(publicUser);

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isTrue();
    }

    @Test
    @DisplayName("Success case for set private user public account")
    void successSetPublicAccount() {
        // given
        given(this.userRepository.save(privateUser)).willReturn(privateUser);

        // when
        PublicScopeResponseDto publicScopeResponseDto = this.userService.changePublicScope(privateUser);

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isFalse();
    }

    @Test
    @DisplayName("Success case for retrieving me")
    void successRetrieveMe() {
        // when
        UserDetailResponseDto userResponseDto = this.userService.retrieveMe(user);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("instagramUrl", "isPrivate")
                .contains("https://instagram.com/instagramId", false);
    }

    @Test
    @DisplayName("Success case for retrieve my account")
    void successRetrieveMyAccount() {

    }

    @Test
    @DisplayName("Success case for retrieving single other user")
    void successRetrieveUser() {
        // given
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(user));
        given(this.postPort.selectPostIdsByUserId("userId")).willReturn(postIds);
        given(this.laonRepository.getUserIdsByLaonId("userId")).willReturn(List.of("publicUserId"));

        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
                UserCenterPreviewResponseDto.of(center.getThumbnailUrl(), center.getName()),
                List.of(ClimbingHistoryResponseDto.from(
                        HoldInfoResponseDto.of(
                                climbingHistory.getHoldInfo().getId(),
                                climbingHistory.getHoldInfo().getName(),
                                climbingHistory.getHoldInfo().getImg(),
                                climbingHistory.getHoldInfo().getCrayonImageUrl()
                        ),
                        climbingHistory.getClimbingCount()
                ))
        );

        given(this.postPort.findClimbingHistoryByPostIds((this.postIds))).willReturn(List.of(historyDto));

        // when
        IndividualUserResponseDto userResponseDto = this.userService.getOtherUserInformation(publicUser, "userNickname");

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting(
                        IndividualUserResponseDto::getHeight,
                        IndividualUserResponseDto::getPostCount,
                        IndividualUserResponseDto::getLaonCount,
                        IndividualUserResponseDto::getIsLaon)
                .contains(175.0F, 1L, 1L, true);

        assertThat(userResponseDto.getCenterClimbingHistories())
                .isNotNull()
                .extracting(
                        center -> center.getCenter().getCenterName(),
                        history -> history.getClimbingHistories().get(0).getClimbingCount())
                .containsExactly(
                        tuple(center.getName(), 2)
                );
    }

    @Test
    @DisplayName("Success case for retrieving single other private user")
    void successRetrievePrivateUser() {
        // given
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(privateUser));
        given(this.postPort.selectPostIdsByUserId("privateUserId")).willReturn(postIds);
        given(this.laonRepository.getUserIdsByLaonId("privateUserId")).willReturn(List.of());

        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
                UserCenterPreviewResponseDto.of(center.getThumbnailUrl(), center.getName()),
                List.of(ClimbingHistoryResponseDto.from(
                        HoldInfoResponseDto.of(
                                climbingHistory.getHoldInfo().getId(),
                                climbingHistory.getHoldInfo().getName(),
                                climbingHistory.getHoldInfo().getImg(),
                                climbingHistory.getHoldInfo().getCrayonImageUrl()
                        ),
                        climbingHistory.getClimbingCount()
                ))
        );

        given(this.postPort.findClimbingHistoryByPostIds((this.postIds))).willReturn(List.of(historyDto));

        // when
        IndividualUserResponseDto userResponseDto = this.userService.getOtherUserInformation(publicUser, "userNickname");

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting(
                        IndividualUserResponseDto::getHeight,
                        IndividualUserResponseDto::getArmReach,
                        IndividualUserResponseDto::getLaonCount,
                        IndividualUserResponseDto::getClimbCount,
                        IndividualUserResponseDto::getIsLaon)
                .contains(null, null, 0L, 2L, false);
    }

    @Test
    @DisplayName("Success case for find posts by user nickname")
    void successFindPosts() {
        // given
        Post post = Post.of(
                center,
                "testContent1",
                user,
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                List.of()
        );
        ReflectionTestUtils.setField(post, "id", "testPostId");
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findByNickname(this.publicUser.getNickname())).willReturn(Optional.of(this.publicUser));
        given(this.blockUserRepository.findBlock(this.publicUser.getId(), user.getId())).willReturn(List.of());
        Pagination<UserPostThumbnailResponseDto> postPagination = paginationFactory.create(new PageImpl<>(
                List.of(UserPostThumbnailResponseDto.from(
                        post.getId(),
                        post.getThumbnailUrl(),
                        post.getCenter().getName(),
                        post.getClimbingHistoryList().stream().map(history ->
                                ClimbingHistoryResponseDto.from(
                                        HoldInfoResponseDto.of(
                                                history.getHoldInfo().getId(),
                                                history.getHoldInfo().getName(),
                                                history.getHoldInfo().getImg(),
                                                history.getHoldInfo().getCrayonImageUrl()
                                        ),
                                        history.getClimbingCount()))
                                .collect(Collectors.toList()))),
                pageable,
                1));
        given(this.postPort.findPostsByUser(this.publicUser, pageable)).willReturn(postPagination);

        // when
        Pagination<UserPostThumbnailResponseDto> dtos = this.userService.findPostsByUser(user, this.publicUser.getNickname(), pageable);

        //then
        assertThat(dtos.getResults())
                .isNotNull()
                .extracting(UserPostThumbnailResponseDto::getPostId, UserPostThumbnailResponseDto::getThumbnailUrl)
                .contains(
                        tuple("testPostId", post.getThumbnailUrl())
                );
    }

    @Test
    @DisplayName("Fail case(user is private) for find posts")
    void failFindPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.userRepository.findByNickname(this.privateUser.getNickname())).willReturn(Optional.of(this.privateUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> this.userService.findPostsByUser(user, this.privateUser.getNickname(), pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privateUser.getNickname()));
    }

    @Test
    @DisplayName("Success case for search user")
    void successSearchUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(List.of(privateUser), pageable, 2);

        given(this.userRepositorySupport.searchUser(user.getId(), privateUser.getNickname(), pageable)).willReturn(userPage);
        given(this.laonRepository.findByLaonIdAndUserId(privateUser.getId(), user.getId())).willReturn(Optional.empty());

        // when
        Pagination<UserPreviewResponseDto> userPreviewResponseDtoPagination = this.userService.searchUser(user, privateUser.getNickname(), pageable);

        // then
        assertThat(userPreviewResponseDtoPagination.getResults())
                .isNotNull()
                .extracting(UserPreviewResponseDto::getNickname, UserPreviewResponseDto::getImagePath)
                .contains(
                        tuple(privateUser.getNickname(), privateUser.getImagePath())
                );
    }

    @Test
    @DisplayName("Success case for modifying single user")
    void successModifyUser() {
        // given
        UserModifyRequestDto dto = new UserModifyRequestDto(
                "nickname",
                175.0F,
                178.0F,
                "",
                "hoonki",
                "dfdf"
        );

        given(this.userRepository.save(this.user)).willReturn(this.user);

        // when
        UserResponseDto userResponseDto = this.userService.modifyUser(user, dto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("email", "nickname")
                .contains("test@gmail.com", "nickname");
    }

    @Test
    @DisplayName("Success case for delete user")
    void successDeleteUser() {
        // given

        // when
        this.userService.delete(this.user);

        // then
    }

    @Test
    @DisplayName("Success case for find history using center and user")
    void successFindByCenterIdAndUserId() {
        // given
        given(this.centerPort.existsByCenterId("centerId")).willReturn(true);

        ClimbingHistoryResponseDto climbingHistoryResponseDto1 = ClimbingHistoryResponseDto.from(
                HoldInfoResponseDto.of(
                        climbingHistory.getHoldInfo().getId(),
                        climbingHistory.getHoldInfo().getName(),
                        climbingHistory.getHoldInfo().getImg(),
                        climbingHistory.getHoldInfo().getCrayonImageUrl()
                ),
                climbingHistory.getClimbingCount()
        );

        ClimbingHistoryResponseDto climbingHistoryResponseDto2 = ClimbingHistoryResponseDto.from(
                HoldInfoResponseDto.of(
                        climbingHistory2.getHoldInfo().getId(),
                        climbingHistory2.getHoldInfo().getName(),
                        climbingHistory2.getHoldInfo().getImg(),
                        climbingHistory2.getHoldInfo().getCrayonImageUrl()
                ),
                climbingHistory2.getClimbingCount()
        );

        List<HistoryByCenterFindResponseDto> histories = List.of(
                HistoryByCenterFindResponseDto.from(
                        post2.getId(),
                        post2.getCreatedAt(),
                        List.of(climbingHistoryResponseDto1)),
                HistoryByCenterFindResponseDto.from(
                        post3.getId(),
                        post3.getCreatedAt(),
                        List.of(climbingHistoryResponseDto2))
        );

        List<HistoryGroupByMonthDto> historyGroup = List.of(HistoryGroupByMonthDto.from(post2.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM")), histories));

        given(this.postPort.findByCenterIdAndUserId("centerId", "userId")).willReturn(historyGroup);

        // when
        List<HistoryGroupByMonthDto> results = this.userService.findHistoryByCenterIdAndUserId(user, center.getId());

        // then
        assertThat(results.get(0))
                .isNotNull()
                .extracting("date", "histories")
                .contains(historyGroup.get(0).getDate(), historyGroup.get(0).getHistories());
    }
}
