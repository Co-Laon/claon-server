package com.claon.user.service;

import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.domain.BlockUser;
import com.claon.user.domain.User;
import com.claon.user.dto.*;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.user.repository.UserRepositorySupport;
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

import java.util.List;
import java.util.Optional;

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

    private final String CENTER_ID = "CENTER_ID";
    private User user, privateUser, publicUser;
    private BlockUser blockUser;

    @BeforeEach
    void setUp() {
        publicUser = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(publicUser, "id", "publicUserId");

        privateUser = User.of(
                "test12@gmail.com",
                "1234567823",
                "private user",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        privateUser.changePublicScope();
        ReflectionTestUtils.setField(privateUser, "id", "privateUserId");

        user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(user, "id", "userId");

        blockUser = BlockUser.of(
                user,
                publicUser
        );
        ReflectionTestUtils.setField(blockUser, "id", "block");
    }

    @Test
    @DisplayName("Success case for set public user private account")
    void successSetPrivateAccount() {
        // given
        given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));
        given(userRepository.save(publicUser)).willReturn(publicUser);

        // when
        var publicScopeResponseDto = userService.changePublicScope(publicUser.getId());

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isTrue();
    }

    @Test
    @DisplayName("Success case for set private user public account")
    void successSetPublicAccount() {
        // given
        given(userRepository.findById(privateUser.getId())).willReturn(Optional.of(privateUser));
        given(userRepository.save(privateUser)).willReturn(privateUser);

        // when
        var publicScopeResponseDto = userService.changePublicScope(privateUser.getId());

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isFalse();
    }

//    @Test
//    @DisplayName("Success case for retrieving me")
//    void successRetrieveMe() {
//        // when
//        UserDetailResponseDto userResponseDto = userService.retrieveMe(user.getId());
//
//        // then
//        assertThat(userResponseDto)
//                .isNotNull()
//                .extracting("instagramUrl", "isPrivate")
//                .contains("https://instagram.com/instagramId", false);
//    }

//    @Test
//    @DisplayName("Success case for retrieving single other user")
//    void successRetrieveUser() {
//        // given
//        given(userRepository.findByNickname("userNickname")).willReturn(Optional.of(user));
//        given(postPort.selectPostIdsByUserId("userId")).willReturn(postIds);
//        given(laonRepository.getUserIdsByLaonId("userId")).willReturn(List.of("publicUserId"));
//
//        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
//                UserCenterPreviewResponseDto.of(center.getThumbnailUrl(), center.getName()),
//                List.of(ClimbingHistoryResponseDto.from(
//                        HoldInfoResponseDto.of(
//                                climbingHistory.getHoldInfo().getId(),
//                                climbingHistory.getHoldInfo().getName(),
//                                climbingHistory.getHoldInfo().getImg(),
//                                climbingHistory.getHoldInfo().getCrayonImageUrl()
//                        ),
//                        climbingHistory.getClimbingCount()
//                ))
//        );
//
//        given(postPort.findClimbingHistoryByPostIds((postIds))).willReturn(List.of(historyDto));
//
//        // when
//        IndividualUserResponseDto userResponseDto = userService.getOtherUserInformation(publicUser, "userNickname");
//
//        // then
//        assertThat(userResponseDto)
//                .isNotNull()
//                .extracting(
//                        IndividualUserResponseDto::getHeight,
//                        IndividualUserResponseDto::getPostCount,
//                        IndividualUserResponseDto::getLaonCount,
//                        IndividualUserResponseDto::getIsLaon)
//                .contains(175.0F, 1L, 1L, true);
//
//        assertThat(userResponseDto.getCenterClimbingHistories())
//                .isNotNull()
//                .extracting(
//                        center -> center.getCenter().getCenterName(),
//                        history -> history.getClimbingHistories().get(0).getClimbingCount())
//                .containsExactly(
//                        tuple(center.getName(), 2)
//                );
//    }

//    @Test
//    @DisplayName("Success case for retrieving single other private user")
//    void successRetrievePrivateUser() {
//        // given
//        given(userRepository.findByNickname("userNickname")).willReturn(Optional.of(privateUser));
//        given(postPort.selectPostIdsByUserId("privateUserId")).willReturn(postIds);
//        given(laonRepository.getUserIdsByLaonId("privateUserId")).willReturn(List.of());
//
//        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
//                UserCenterPreviewResponseDto.of(center.getThumbnailUrl(), center.getName()),
//                List.of(ClimbingHistoryResponseDto.from(
//                        HoldInfoResponseDto.of(
//                                climbingHistory.getHoldInfo().getId(),
//                                climbingHistory.getHoldInfo().getName(),
//                                climbingHistory.getHoldInfo().getImg(),
//                                climbingHistory.getHoldInfo().getCrayonImageUrl()
//                        ),
//                        climbingHistory.getClimbingCount()
//                ))
//        );
//
//        given(postPort.findClimbingHistoryByPostIds((postIds))).willReturn(List.of(historyDto));
//
//        // when
//        IndividualUserResponseDto userResponseDto = userService.getOtherUserInformation(publicUser, "userNickname");
//
//        // then
//        assertThat(userResponseDto)
//                .isNotNull()
//                .extracting(
//                        IndividualUserResponseDto::getHeight,
//                        IndividualUserResponseDto::getArmReach,
//                        IndividualUserResponseDto::getLaonCount,
//                        IndividualUserResponseDto::getClimbCount,
//                        IndividualUserResponseDto::getIsLaon)
//                .contains(null, null, 0L, 2L, false);
//    }

//    @Test
//    @DisplayName("Success case for find posts by user nickname")
//    void successFindPosts() {
//        // given
//        Post post = Post.of(
//                center,
//                "testContent1",
//                user,
//                List.of(PostContents.of(
//                        "test.com/test.png"
//                )),
//                List.of()
//        );
//        ReflectionTestUtils.setField(post, "id", "testPostId");
//        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());
//
//        Pageable pageable = PageRequest.of(0, 2);
//        given(userRepository.findByNickname(publicUser.getNickname())).willReturn(Optional.of(publicUser));
//        given(blockUserRepository.findBlock(publicUser.getId(), user.getId())).willReturn(List.of());
//        Pagination<UserPostThumbnailResponseDto> postPagination = paginationFactory.create(new PageImpl<>(
//                List.of(UserPostThumbnailResponseDto.from(
//                        post.getId(),
//                        post.getThumbnailUrl(),
//                        post.getCenter().getName(),
//                        post.getClimbingHistoryList().stream().map(history ->
//                                ClimbingHistoryResponseDto.from(
//                                        HoldInfoResponseDto.of(
//                                                history.getHoldInfo().getId(),
//                                                history.getHoldInfo().getName(),
//                                                history.getHoldInfo().getImg(),
//                                                history.getHoldInfo().getCrayonImageUrl()
//                                        ),
//                                        history.getClimbingCount()))
//                                .collect(Collectors.toList()))),
//                pageable,
//                1));
//        given(postPort.findPostsByUser(publicUser, pageable)).willReturn(postPagination);
//
//        // when
//        Pagination<UserPostThumbnailResponseDto> dtos = userService.findPostsByUser(user, publicUser.getNickname(), pageable);
//
//        //then
//        assertThat(dtos.getResults())
//                .isNotNull()
//                .extracting(UserPostThumbnailResponseDto::getPostId, UserPostThumbnailResponseDto::getThumbnailUrl)
//                .contains(
//                        tuple("testPostId", post.getThumbnailUrl())
//                );
//    }

    @Test
    @DisplayName("Fail case(user is private) for find posts")
    void failFindPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(userRepository.findByNickname(privateUser.getNickname())).willReturn(Optional.of(privateUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.findPostsByUser(user.getId(), privateUser.getNickname(), pageable)
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

        given(userRepositorySupport.searchUser(user.getId(), privateUser.getNickname(), pageable)).willReturn(userPage);
        given(laonRepository.findByLaonIdAndUserId(privateUser.getId(), user.getId())).willReturn(Optional.empty());

        // when
        var userPreviewResponseDtoPagination = userService.searchUser(user.getId(), privateUser.getNickname(), pageable);

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

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        // when
        UserResponseDto userResponseDto = userService.modifyUser(user.getId(), dto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("email", "nickname")
                .contains(user.getEmail(), dto.getNickname());
    }

    @Test
    @DisplayName("Success case for delete user")
    void successDeleteUser() {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        userService.delete(user.getId());

        // then
    }

//    @Test
//    @DisplayName("Success case for find history using center and user")
//    void successFindByCenterIdAndUserId() {
//        // given
//        given(userRepository.findByNickname(user.getNickname())).willReturn(Optional.of(user));
//        given(centerPort.existsByCenterId(center.getId())).willReturn(true);
//
//        ClimbingHistoryResponseDto climbingHistoryResponseDto3 = ClimbingHistoryResponseDto.from(
//                HoldInfoResponseDto.of(
//                        climbingHistory4.getHoldInfo().getId(),
//                        climbingHistory4.getHoldInfo().getName(),
//                        climbingHistory4.getHoldInfo().getImg(),
//                        climbingHistory4.getHoldInfo().getCrayonImageUrl()
//                ),
//                climbingHistory4.getClimbingCount()
//        );
//
//        List<HistoryByCenterFindResponseDto> histories = List.of(
//                HistoryByCenterFindResponseDto.from(
//                        post5.getId(),
//                        post5.getCreatedAt(),
//                        List.of(climbingHistoryResponseDto3))
//        );
//
//        List<HistoryGroupByMonthDto> historyGroup = List.of(HistoryGroupByMonthDto.from(post3.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM")), histories));
//
//        given(postPort.findByCenterIdAndUserId(center.getId(), user3.getId())).willReturn(historyGroup);
//
//        // when
//        List<HistoryGroupByMonthDto> results = userService.findHistoryByCenterIdAndUserId(user, user3.getNickname(), center.getId());
//
//        // then
//        assertThat(results.get(0))
//                .isNotNull()
//                .extracting("date", "histories")
//                .contains(historyGroup.get(0).getDate(), historyGroup.get(0).getHistories());
//    }

    @Test
    @DisplayName("failure case for find history using center and user because of block relation")
    void failureFindByCenterIdAndUserId_forPrivateUser() {
        // given
        given(userRepository.findByNickname(privateUser.getNickname())).willReturn(Optional.of(privateUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.findHistoryByCenterIdAndUserId(user.getId(), privateUser.getNickname(), CENTER_ID)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privateUser.getNickname()));
    }

//    @Test
//    @DisplayName("Success case for finding center history by user nickname")
//    void successFindCenterHistory() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        Page<UserCenterResponseDto> postPagination = new PageImpl<>(
//                List.of(UserCenterResponseDto.from(
//                        center.getId(),
//                        center.getThumbnailUrl(),
//                        center.getName()
//                )), pageable, 1);
//
//        given(userRepository.findByNickname(user.getNickname())).willReturn(Optional.of(user));
//        given(postPort.selectDistinctCenterByUser(user, pageable)).willReturn(postPagination);
//
//        // when
//        Pagination<UserCenterResponseDto> postHistory = userService.findCenterHistory(user, user.getNickname(), pageable);
//        // then
//        assertThat(postHistory.getResults().size()).isEqualTo(1);
//    }

//    @Test
//    @DisplayName("Success case for find history using year, month")
//    void successFindHistoryByDate() {
//        // given
//        CenterInfoResponseDto centerInfo = CenterInfoResponseDto.from(
//                center.getId(),
//                center.getName(),
//                center.getThumbnailUrl()
//        );
//
//        List<ClimbingHistoryResponseDto> histories = List.of(
//                ClimbingHistoryResponseDto.from(
//                        HoldInfoResponseDto.of(
//                                climbingHistory4.getHoldInfo().getId(),
//                                climbingHistory4.getHoldInfo().getName(),
//                                climbingHistory4.getHoldInfo().getImg(),
//                                climbingHistory4.getHoldInfo().getCrayonImageUrl()
//                        ),
//                        climbingHistory4.getClimbingCount()
//                )
//        );
//
//        HistoryByDateFindResponseDto historyDto = HistoryByDateFindResponseDto.from(
//                centerInfo, histories
//        );
//
//        given(userRepository.findByNickname(user3.getNickname())).willReturn(Optional.of(user3));
//        given(postPort.findHistoryByDate(user3.getId(), LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue())).willReturn(List.of(historyDto));
//
//        // when
//        List<HistoryByDateFindResponseDto> results = userService.findHistoryByDateAndUserId(user, user3.getNickname(), LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
//
//        // then
//        assertThat(results.get(0))
//                .isNotNull()
//                .extracting(HistoryByDateFindResponseDto::getCenterInfo, HistoryByDateFindResponseDto::getHistories)
//                .contains(centerInfo, histories);
//    }

    @Test
    @DisplayName("failure case for find history using center and user because of block relation")
    void failureFindHistoryByDate_forBlockUser() {
        // given
        given(userRepository.findByNickname(publicUser.getNickname())).willReturn(Optional.of(publicUser));
        given(blockUserRepository.findBlock(publicUser.getId(), user.getId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.findHistoryByCenterIdAndUserId(user.getId(), publicUser.getNickname(), CENTER_ID)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", user.getNickname()));
    }
}
