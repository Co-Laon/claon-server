package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.user.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.CenterPreviewResponseDto;
import coLaon.ClaonBack.user.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HoldInfoResponseDto;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    UserService userService;

    private User user, privateUser, publicUser;
    private Center center;
    private ClimbingHistory climbingHistory;
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
        HoldInfo holdInfo = HoldInfo.of("name", "dfdf", center);
        ReflectionTestUtils.setField(holdInfo, "id", "test");
        this.climbingHistory = ClimbingHistory.of(
                post,
                holdInfo,
                2
        );
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
        UserResponseDto userResponseDto = this.userService.getUser(user);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("email", "instagramUserName", "isPrivate")
                .contains("test@gmail.com", "instagramId", false);
    }

    @Test
    @DisplayName("Success case for retrieving single other user")
    void successRetrieveUser() {
        // given
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(user));
        given(this.postPort.selectPostIdsByUserId("userId")).willReturn(postIds);
        given(this.laonRepository.getUserIdsByLaonId("userId")).willReturn(List.of("publicUserId"));

        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
                CenterPreviewResponseDto.of(center.getThumbnailUrl(), center.getName()),
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
                CenterPreviewResponseDto.of(center.getThumbnailUrl(), center.getName()),
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
                Set.of()
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
                        post.getClimbingHistorySet().stream().map(history ->
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
        final BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> this.userService.findPostsByUser(user, this.privateUser.getNickname(), pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "비공개 계정입니다.");
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
}
