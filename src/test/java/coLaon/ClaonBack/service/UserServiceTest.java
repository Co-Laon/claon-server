package coLaon.ClaonBack.service;


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
import coLaon.ClaonBack.user.dto.PublicUserResponseDto;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    UserService userService;

    private User user, privateUser, publicUser, blockUser;
    private Post post;
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
    }

    @Test
    @DisplayName("Success case for retrieving me")
    void successRetrieveMe() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));

        // when
        UserResponseDto userResponseDto = this.userService.getUser("userId");

        // then
        assertThat(userResponseDto.getEmail()).isEqualTo("test@gmail.com");
        assertThat(userResponseDto.getInstagramUserName()).isEqualTo("instagramId");
        assertThat(userResponseDto.getIsPrivate()).isFalse();
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
        assertThat(userResponseDto.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    @DisplayName("Success case for retrieving single other user")
    void successRetrieveUser() {
        // given
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));
        List<String> givenList = new ArrayList<>(Arrays.asList("12","23"));
        given(this.postRepository.selectPostIdsByUserId("userId")).willReturn(givenList);
        given(this.postLikeRepository.countByPostIdIn(givenList)).willReturn(5L);
        Set<String> laonIds = new HashSet<>();
        laonIds.add("publicUserId");
        given(this.laonRepository.getLaonIdsByUserId("userId")).willReturn(laonIds);

        // when
        PublicUserResponseDto userResponseDto = this.userService.getOtherUserInformation("publicUserId","userId");

        // then
        assertThat(userResponseDto.getMetropolitanActiveArea()).isEqualTo("경기도");
        assertThat(userResponseDto.getPostCount()).isEqualTo(2L);
        assertThat(userResponseDto.getLikeCount()).isEqualTo(5L);
        assertThat(userResponseDto.getLaonCount()).isEqualTo(1L);
        assertThat(userResponseDto.getIsLaon()).isEqualTo(true);

        // given
        given(this.userRepository.findById("privateUserId")).willReturn(Optional.of(privateUser));
        List<String> givenList1 = new ArrayList<>(Arrays.asList("12","23"));
        given(this.postRepository.selectPostIdsByUserId("privateUserId")).willReturn(givenList1);
        given(this.postLikeRepository.countByPostIdIn(givenList)).willReturn(5L);
        Set<String> laonIds1 = new HashSet<>();
        given(this.laonRepository.getLaonIdsByUserId("privateUserId")).willReturn(laonIds1);

        // when
        PublicUserResponseDto userResponseDto1 = this.userService.getOtherUserInformation("publicUserId", "privateUserId");

        // then
        assertThat(userResponseDto1.getMetropolitanActiveArea()).isEqualTo(null);
        assertThat(userResponseDto1.getBasicLocalActiveArea()).isEqualTo(null);
        assertThat(userResponseDto1.getLaonCount()).isEqualTo(0);
        assertThat(userResponseDto1.getIsLaon()).isEqualTo(false);
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
        assertThat(blockUserFindResponseDto.getResults().size()).isEqualTo(1);
        assertThat(blockUserFindResponseDto.getResults().get(0).getBlockUserNickName()).isEqualTo("testBlockNickname");
    }
}
