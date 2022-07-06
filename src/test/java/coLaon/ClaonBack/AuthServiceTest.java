package coLaon.ClaonBack;

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
                "test@gmail.com",
                "test",
                "경기도",
                "성남시",
                "",
                "123456",
                "test"
        );
    }

    @Test
    @DisplayName("Success case for sign up")
    void successSignUp() {
        // given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                "test",
                "경기도",
                "성남시",
                "",
                "123456",
                "test"
        );

        given(this.userRepository.findById("test")).willReturn(Optional.of(this.user));
        given(this.userRepository.findByNickname("test")).willReturn(Optional.empty());

        given(this.userRepository.save(this.user)).willReturn(this.user);

        // when
        UserResponseDto userResponseDto = this.userService.signUp(this.user.getId(), signUpRequestDto);

        // then
        assertThat(userResponseDto).isNotNull();
        assertThat(userResponseDto)
                .extracting("id", "email", "nickname")
                .contains("test", "test@gmail.com", "test");
    }
}
