package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.domain.User;
import com.claon.user.dto.*;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.user.repository.UserRepositorySupport;
import com.claon.user.service.client.PostClient;
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
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
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
    PostClient postClient;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    UserService userService;

    private RequestUserInfo USER_INFO, PUBLIC_USER_INFO;
    private final String CENTER_ID = "CENTER_ID";
    private final String HOLD_ID = "HOLD_ID";
    private User user, publicUser;

    @BeforeEach
    void setUp() {
        publicUser = User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(publicUser, "id", "publicUserId");

        user = User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(user, "id", "userId");

        USER_INFO = new RequestUserInfo(user.getId());
        PUBLIC_USER_INFO = new RequestUserInfo(publicUser.getId());
    }

    @Test
    @DisplayName("Success case for retrieving me")
    void successRetrieveMe() {
        given(userRepository.findById(USER_INFO.id())).willReturn(Optional.of(user));
        given(laonRepository.getUserIdsByLaonId(USER_INFO.id())).willReturn(List.of(PUBLIC_USER_INFO.id()));

        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
                CENTER_ID,
                0,
                List.of(ClimbingHistoryResponseDto.from(
                        HOLD_ID,
                        0
                ))
        );

        given(postClient.findHistoriesByUserId(USER_INFO.id())).willReturn(List.of(historyDto));

        // when
        UserDetailResponseDto userResponseDto = userService.retrieveMe(USER_INFO);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("nickname", "laonCount")
                .contains(user.getNickname(), 1L);
    }

    @Test
    @DisplayName("Success case for retrieving single other user")
    void successRetrieveUser() {
        // given
        given(userRepository.findById(USER_INFO.id())).willReturn(Optional.of(user));
        given(laonRepository.getUserIdsByLaonId(USER_INFO.id())).willReturn(List.of(PUBLIC_USER_INFO.id()));

        CenterClimbingHistoryResponseDto historyDto = CenterClimbingHistoryResponseDto.from(
                CENTER_ID,
                0,
                List.of(ClimbingHistoryResponseDto.from(
                        HOLD_ID,
                        0
                ))
        );

        given(postClient.findHistoriesByUserId(USER_INFO.id())).willReturn(List.of(historyDto));

        // when
        IndividualUserResponseDto userResponseDto = userService.getOtherUserInformation(PUBLIC_USER_INFO, USER_INFO.id());

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting(
                        IndividualUserResponseDto::getHeight,
                        IndividualUserResponseDto::getLaonCount,
                        IndividualUserResponseDto::getIsLaon)
                .contains(user.getHeight(), 1L, true);
        assertThat(userResponseDto.getCenterClimbingHistories())
                .isNotNull()
                .extracting(
                        CenterClimbingHistoryResponseDto::getCenterId,
                        history -> history.getClimbingHistories().get(0).getClimbingCount())
                .containsExactly(
                        tuple(CENTER_ID, 0)
                );
    }

    @Test
    @DisplayName("Success case for find posts by user nickname")
    void successFindPosts() {
        // given
        String POST_ID = "POST_ID";

        Pageable pageable = PageRequest.of(0, 2);
        given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));
        given(blockUserRepository.findBlock(publicUser.getId(), user.getId())).willReturn(List.of());
        Pagination<UserPostThumbnailResponseDto> postPagination = paginationFactory.create(new PageImpl<>(
                List.of(UserPostThumbnailResponseDto.from(
                        POST_ID,
                        "",
                        List.of(ClimbingHistoryResponseDto.from(
                                HOLD_ID,
                                0
                        )))),
                pageable,
                1));
        given(postClient.findPostThumbnails(publicUser.getId(), pageable)).willReturn(postPagination);

        // when
        Pagination<UserPostThumbnailResponseDto> dtos = userService.findPostsByUser(USER_INFO, publicUser.getId(), pageable);

        //then
        assertThat(dtos.getResults())
                .isNotNull()
                .extracting(UserPostThumbnailResponseDto::getPostId, UserPostThumbnailResponseDto::getThumbnailUrl)
                .contains(
                        tuple(POST_ID, "")
                );
    }

    @Test
    @DisplayName("Success case for search user")
    void successSearchUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> userPage = new PageImpl<>(List.of(publicUser), pageable, 2);

        given(userRepositorySupport.searchUser(user.getId(), publicUser.getNickname(), pageable)).willReturn(userPage);
        given(laonRepository.findByLaonIdAndUserId(publicUser.getId(), user.getId())).willReturn(Optional.empty());

        // when
        var userPreviewResponseDtoPagination = userService.searchUser(USER_INFO, publicUser.getNickname(), pageable);

        // then
        assertThat(userPreviewResponseDtoPagination.getResults())
                .isNotNull()
                .extracting(UserPreviewResponseDto::getNickname)
                .contains(
                        publicUser.getNickname()
                );
    }

    @Test
    @DisplayName("Success case for modifying single user")
    void successModifyUser() {
        // given
        UserModifyRequestDto dto = new UserModifyRequestDto(
                "nickname",
                175.0F,
                178.0F
        );

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        // when
        UserResponseDto userResponseDto = userService.modifyUser(USER_INFO, dto);

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
        userService.delete(USER_INFO);

        // then
    }
}
