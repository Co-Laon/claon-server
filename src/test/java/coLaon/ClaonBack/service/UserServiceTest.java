package coLaon.ClaonBack.service;


import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User user, privateUser, publicUser;

    @BeforeEach
    void setUp() {
        this.publicUser = User.createNewUser(
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

        this.privateUser = User.createNewUser(
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
        this.user = User.createNewUser(
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
    }

    @Test
    @DisplayName("Success case for retrieving single user")
    void successRetrieveUser() {
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
        given(this.userRepository.findById("userId")).willReturn(Optional.of(user));

        // when
        UserModifyRequestDto dto = new UserModifyRequestDto("nickname", "seoul", "seoul", "", "hoonki", "dfdf", true);
        this.userService.modifyUser(user.getId(), dto);
    }

    @Test
    @DisplayName("Success case for set public user private account")
    void successSetPrivateAccount() {
        // given
        given(this.userRepository.findById("publicUserId")).willReturn(Optional.of(publicUser));
        given(this.userRepository.save(publicUser)).willReturn(publicUser);

        // when
        PublicScopeResponseDto publicScopeResponseDto = this.userService.setPublicScope("publicUserId");

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
        PublicScopeResponseDto publicScopeResponseDto = this.userService.setPublicScope("privateUserId");

        // then
        assertThat(publicScopeResponseDto.getIsPrivate()).isFalse();
    }

}
