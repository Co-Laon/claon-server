package coLaon.ClaonBack.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.BlockUserFindResponseDto;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.BlockUserService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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

    private User privateUser, publicUser, blockUser;
    private BlockUser blockUserRelation;
    private Laon laonRelation;

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
            this.blockUserService.createBlock("publicUserId", "testBlockNickname");

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
        this.blockUserService.deleteBlock("publicUserId", "testBlockNickname");

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
        Pagination<BlockUserFindResponseDto> blockUserFindResponseDto = this.blockUserService.findBlockUser("publicUserId", pageable);

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
