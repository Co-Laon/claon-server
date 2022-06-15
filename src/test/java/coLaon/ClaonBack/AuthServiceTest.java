package coLaon.ClaonBack;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "test",
                "01012341234",
                "test@gmail.com",
                "test1234!!",
                "test",
                "경기도",
                "성남시",
                "",
                "test"
        );
    }

    @Test
    @DisplayName("Success case for sign up with password")
    void successSignUpWithPassword() {
        try (MockedStatic<User> mockedUser = mockStatic(User.class)) {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                    "01012341234",
                    "test@gmail.com",
                    "test1234!!",
                    "test",
                    "경기도",
                    "성남시",
                    "",
                    "test"
            );

            given(this.userRepository.findByEmail("test@gmail.com")).willReturn(Optional.empty());
            given(this.userRepository.findByNickname("test")).willReturn(Optional.empty());

            this.user.setPassword(null);
            given(User.of(
                    "01012341234",
                    "test@gmail.com",
                    "test1234!!",
                    "test",
                    "경기도",
                    "성남시",
                    "",
                    "test")).willReturn(this.user);

            given(this.userRepository.save(this.user)).willReturn(this.user);

            // when
            UserResponseDto userResponseDto = this.userService.signUp(signUpRequestDto);

            // then
            assertThat(userResponseDto).isNotNull();
            assertThat(userResponseDto)
                    .extracting("id", "phoneNumber", "email", "nickname")
                    .contains("test", "01012341234", "test@gmail.com", "test");
        }
    }

    @Test
    @DisplayName("Success case for sign up without password")
    void successSignUpWithoutPassword() {
        try (MockedStatic<User> mockedUser = mockStatic(User.class)) {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                    "01012341234",
                    "test@gmail.com",
                    null,
                    "test",
                    "경기도",
                    "성남시",
                    "",
                    "test"
            );

            given(this.userRepository.findByEmail("test@gmail.com")).willReturn(Optional.empty());
            given(this.userRepository.findByNickname("test")).willReturn(Optional.empty());

            given(User.of(
                    "01012341234",
                    "test@gmail.com",
                    null,
                    "test",
                    "경기도",
                    "성남시",
                    "",
                    "test")).willReturn(this.user);

            given(this.userRepository.save(this.user)).willReturn(this.user);

            // when
            UserResponseDto userResponseDto = this.userService.signUp(signUpRequestDto);

            // then
            assertThat(userResponseDto).isNotNull();
            assertThat(userResponseDto)
                    .extracting("id", "phoneNumber", "email", "nickname")
                    .contains("test", "01012341234", "test@gmail.com", "test");
        }
    }

    @Test
    @DisplayName("Invalid password format for sign up")
    void failSignUpPasswordFormatInvalid() {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "01012341234",
                "test@gmail.com",
                "test1234",
                "test",
                "경기도",
                "성남시",
                "",
                "test"
        );

        given(this.userRepository.findByEmail("test@gmail.com")).willReturn(Optional.empty());
        given(this.userRepository.findByNickname("test")).willReturn(Optional.empty());

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> this.userService.signUp(signUpRequestDto));

        // then
        assertThat(exception).isInstanceOf(BadRequestException.class);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_FORMAT);
    }
}
