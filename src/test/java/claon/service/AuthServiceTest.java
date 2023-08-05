package claon.service;

import claon.common.domain.JwtDto;
import claon.common.utils.JwtUtil;
import claon.user.domain.enums.OAuth2Provider;
import claon.user.domain.User;
import claon.user.dto.OAuth2UserInfoDto;
import claon.user.dto.SignInRequestDto;
import claon.user.dto.SignUpRequestDto;
import claon.user.dto.UserResponseDto;
import claon.user.repository.UserRepository;
import claon.user.infra.OAuth2UserInfoProvider;
import claon.user.service.OAuth2UserInfoProviderSupplier;
import claon.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    OAuth2UserInfoProviderSupplier oAuth2UserInfoProviderSupplier;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    OAuth2UserInfoProvider oAuth2UserInfoProvider;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "123456",
                "test"
        );
        ReflectionTestUtils.setField(this.user, "id", "test");
    }

    @Test
    @DisplayName("Success case for sign in for completed user")
    void successSignInForCompletedUser() {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(
                "testCode"
        );

        OAuth2UserInfoDto oAuth2UserInfoDto = OAuth2UserInfoDto.of(
                "1234567890",
                "test@gmail.com"
        );

        JwtDto jwtDto = JwtDto.of(
                "access-token",
                "refresh-token",
                true
        );

        given(this.oAuth2UserInfoProviderSupplier.getProvider(OAuth2Provider.GOOGLE)).willReturn(this.oAuth2UserInfoProvider);
        given(this.oAuth2UserInfoProvider.getUserInfo(signInRequestDto.getCode())).willReturn(oAuth2UserInfoDto);

        given(this.userRepository.findByEmailAndOAuthId(oAuth2UserInfoDto.getEmail(), oAuth2UserInfoDto.getOAuthId())).willReturn(Optional.of(this.user));
        given(this.jwtUtil.createToken(this.user.getId(), true)).willReturn(jwtDto);

        // when
        JwtDto result = this.userService.signIn(OAuth2Provider.GOOGLE, signInRequestDto);

        // then
        assertThat(result)
                .isNotNull()
                .extracting("accessToken", "refreshToken")
                .contains("access-token", "refresh-token");
    }

    @Test
    @DisplayName("Success case for sign in for first access user")
    void successSignInForFirstAccessUser() {
        User firstAccessUser = User.of(
                "test@gmail.com",
                "1234567890",
                "nickname",
                null,
                null,
                null,
                null,
                null
        );
        ReflectionTestUtils.setField(firstAccessUser, "id", "test");

        try (MockedStatic<User> mockedUser = mockStatic(User.class)) {
            // given
            SignInRequestDto signInRequestDto = new SignInRequestDto(
                    "testCode"
            );

            OAuth2UserInfoDto oAuth2UserInfoDto = OAuth2UserInfoDto.of(
                    "1234567890",
                    "test@gmail.com"
            );

            JwtDto jwtDto = JwtDto.of(
                    "access-token",
                    "refresh-token",
                    false
            );

            given(this.oAuth2UserInfoProviderSupplier.getProvider(OAuth2Provider.GOOGLE)).willReturn(this.oAuth2UserInfoProvider);
            given(this.oAuth2UserInfoProvider.getUserInfo(signInRequestDto.getCode())).willReturn(oAuth2UserInfoDto);

            given(this.userRepository.findByEmailAndOAuthId(oAuth2UserInfoDto.getEmail(), oAuth2UserInfoDto.getOAuthId())).willReturn(Optional.empty());

            mockedUser.when(() -> User.createNewUser("test@gmail.com", "1234567890")).thenReturn(firstAccessUser);
            given(this.userRepository.save(firstAccessUser)).willReturn(firstAccessUser);

            given(this.jwtUtil.createToken(this.user.getId(), true)).willReturn(jwtDto);

            // when
            JwtDto result = this.userService.signIn(OAuth2Provider.GOOGLE, signInRequestDto);

            // then
            assertThat(result)
                    .isNotNull()
                    .extracting("accessToken", "refreshToken")
                    .contains("access-token", "refresh-token");
        }
    }

    @Test
    @DisplayName("Success case for sign up")
    void successSignUp() {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "test",
                175.0F,
                178.0F,
                "",
                "123456",
                "test"
        );

        given(this.userRepository.save(this.user)).willReturn(this.user);

        // when
        UserResponseDto userResponseDto = this.userService.signUp(this.user, signUpRequestDto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .extracting("email", "nickname")
                .contains("test@gmail.com", "test");
    }
}
