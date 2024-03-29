package com.claon.user.service;

import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.domain.BlockUser;
import com.claon.user.domain.Laon;
import com.claon.user.domain.User;
import com.claon.user.dto.BlockUserResponseDto;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class BlockUserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    BlockUserRepository blockUserRepository;
    @Mock
    LaonRepository laonRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    BlockUserService blockUserService;

    private RequestUserInfo PUBLIC_USER_INFO;
    private User publicUser, blockUser;
    private BlockUser blockUserRelation;
    private Laon laonRelation;

    @BeforeEach
    void setUp() {
        publicUser = User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(publicUser, "id", "publicUserId");

        blockUser = User.of(
                "block@gmail.com",
                "testBlockNickname",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(blockUser, "id", "blockUserId");

        PUBLIC_USER_INFO = new RequestUserInfo(publicUser.getId());

        blockUserRelation = BlockUser.of(
                publicUser,
                blockUser
        );

        laonRelation = Laon.of(
                publicUser,
                blockUser
        );
    }

    @Test
    @DisplayName("Success case for block user")
    void successBlockUser() {
        try (MockedStatic<BlockUser> mockedBlock = mockStatic(BlockUser.class)) {
            // given
            given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));
            given(userRepository.findById(blockUser.getId())).willReturn(Optional.of(blockUser));
            given(blockUserRepository.findByUserIdAndBlockId(publicUser.getId(), blockUser.getId())).willReturn(Optional.empty());
            given(laonRepository.findByLaonIdAndUserId(blockUser.getId(), publicUser.getId())).willReturn(Optional.of(laonRelation));

            mockedBlock.when(() -> BlockUser.of(publicUser, blockUser)).thenReturn(blockUserRelation);

            given(blockUserRepository.save(blockUserRelation)).willReturn(blockUserRelation);

            // when
            blockUserService.createBlock(PUBLIC_USER_INFO, blockUser.getId());

            // then
            assertThat(blockUserRepository.findByUserIdAndBlockId(publicUser.getId(), blockUser.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Failure case for create block when block myself")
    void failCreateBlockMyself() {
        //given
        given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));
        given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));

        //when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> blockUserService.createBlock(PUBLIC_USER_INFO, publicUser.getId())
        );

        //then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("자신을 %s할 수 없습니다.", BlockUser.domain));
    }

    @Test
    @DisplayName("Success case for unblock user")
    void successUnblockUser() {
        // given
        given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));
        given(userRepository.findById(blockUser.getId())).willReturn(Optional.of(blockUser));
        given(blockUserRepository.findByUserIdAndBlockId(publicUser.getId(), blockUser.getId())).willReturn(Optional.of(blockUserRelation));

        // when
        blockUserService.deleteBlock(PUBLIC_USER_INFO, blockUser.getId());

        // then
        assertThat(blockUserRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Success case for find block users")
    void successFindBlockUsers() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<BlockUser> blockUsers = new PageImpl<>(List.of(blockUserRelation), pageable, 2);

        given(userRepository.findById(publicUser.getId())).willReturn(Optional.of(publicUser));
        given(blockUserRepository.findByUser(publicUser, pageable)).willReturn(blockUsers);

        // when
        var blockUserFindResponseDto = blockUserService.findBlockUser(PUBLIC_USER_INFO, pageable);

        // then
        assertThat(blockUserFindResponseDto.getResults())
                .isNotNull()
                .extracting(
                        BlockUserResponseDto::getBlockUserNickName
                )
                .contains(
                        blockUser.getNickname()
                );
    }
}
