package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-up")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserResponseDto signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return this.userService.signUp(signUpRequestDto);
    }
}
