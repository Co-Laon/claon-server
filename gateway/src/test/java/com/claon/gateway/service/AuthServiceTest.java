package com.claon.gateway.service;

import com.claon.gateway.common.domain.JwtDto;
import com.claon.gateway.common.utils.JwtUtil;
import com.claon.gateway.domain.User;
import com.claon.gateway.domain.enums.OAuth2Provider;
import com.claon.gateway.dto.OAuth2UserInfoDto;
import com.claon.gateway.dto.SignInRequestDto;
import com.claon.gateway.dto.SignUpRequestDto;
import com.claon.gateway.infra.OAuth2UserInfoProvider;
import com.claon.gateway.repository.UserRepository;
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
        user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "123456",
                "test"
        );
        ReflectionTestUtils.setField(user, "id", "test");
    }

    @Test
    @DisplayName("Success case for sign in")
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
                "refresh-token"
        );

        given(oAuth2UserInfoProviderSupplier.getProvider(OAuth2Provider.GOOGLE)).willReturn(oAuth2UserInfoProvider);
        given(oAuth2UserInfoProvider.getUserInfo(signInRequestDto.getCode())).willReturn(oAuth2UserInfoDto);

        given(userRepository.findByEmailAndOAuthId(oAuth2UserInfoDto.getEmail(), oAuth2UserInfoDto.getOAuthId())).willReturn(Optional.of(user));
        given(jwtUtil.createToken(user.getId())).willReturn(jwtDto);

        // when
        var result = userService.signIn(OAuth2Provider.GOOGLE, signInRequestDto);

        // then
        assertThat(result)
                .isNotNull()
                .extracting("accessToken", "refreshToken")
                .contains(result.getAccessToken(), result.getRefreshToken());
    }

    @Test
    @DisplayName("Success case for sign up")
    void successSignUp() {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "testCode",
                "test",
                175.0F,
                178.0F,
                "",
                "123456",
                "test"
        );

        OAuth2UserInfoDto oAuth2UserInfoDto = OAuth2UserInfoDto.of(
                "1234567890",
                "test@gmail.com"
        );

        JwtDto jwtDto = JwtDto.of(
                "access-token",
                "refresh-token"
        );

        try (MockedStatic<User> mockedUser = mockStatic(User.class)) {

            given(oAuth2UserInfoProviderSupplier.getProvider(OAuth2Provider.GOOGLE)).willReturn(oAuth2UserInfoProvider);
            given(oAuth2UserInfoProvider.getUserInfo(signUpRequestDto.getCode())).willReturn(oAuth2UserInfoDto);
            mockedUser.when(() -> User.signUp(
                    "test@gmail.com",
                    "1234567890",
                    "test",
                    175.0F,
                    178.0F,
                    "",
                    "123456",
                    "test"
            )).thenReturn(user);
            given(userRepository.save(user)).willReturn(user);

            given(jwtUtil.createToken(user.getId())).willReturn(jwtDto);

            // when
            var result = userService.signUp(OAuth2Provider.GOOGLE, signUpRequestDto);

            // then
            assertThat(result)
                    .isNotNull()
                    .extracting("accessToken", "refreshToken")
                    .contains(result.getAccessToken(), result.getRefreshToken());
        }
    }
}
