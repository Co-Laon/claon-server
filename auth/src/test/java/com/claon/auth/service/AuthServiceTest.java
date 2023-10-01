package com.claon.auth.service;

import com.claon.auth.common.domain.JwtDto;
import com.claon.auth.common.utils.JwtUtil;
import com.claon.auth.domain.User;
import com.claon.auth.dto.request.SignInRequestDto;
import com.claon.auth.dto.request.SignUpRequestDto;
import com.claon.auth.repository.UserRepository;
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
    JwtUtil jwtUtil;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.of(
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        );
        ReflectionTestUtils.setField(user, "id", "test");
    }

    @Test
    @DisplayName("Success case for sign in")
    void successSignInForCompletedUser() {
        // given
        SignInRequestDto signInRequestDto = new SignInRequestDto(
                "test@gmail.com"
        );

        JwtDto jwtDto = JwtDto.of(
                "access-token",
                "refresh-token"
        );

        given(userRepository.findByEmail(signInRequestDto.email())).willReturn(Optional.of(user));
        given(jwtUtil.createToken(user.getId())).willReturn(jwtDto);

        // when
        var result = userService.signIn(signInRequestDto);

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
                "test@gmail.com",
                "test",
                175.0F,
                178.0F
        );

        JwtDto jwtDto = JwtDto.of(
                "access-token",
                "refresh-token"
        );

        try (MockedStatic<User> mockedUser = mockStatic(User.class)) {
            mockedUser.when(() -> User.signUp(
                    "test@gmail.com",
                    "test",
                    175.0F,
                    178.0F
            )).thenReturn(user);
            given(userRepository.save(user)).willReturn(user);

            given(jwtUtil.createToken(user.getId())).willReturn(jwtDto);

            // when
            var result = userService.signUp(signUpRequestDto);

            // then
            assertThat(result)
                    .isNotNull()
                    .extracting("accessToken", "refreshToken")
                    .contains(result.getAccessToken(), result.getRefreshToken());
        }
    }
}
