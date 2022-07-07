package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    @GetMapping("/nickname/{nickname}/duplicate-check")
    @ResponseStatus(value = HttpStatus.OK)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(@PathVariable String nickname) {
        return this.userService.nicknameDuplicatedCheck(nickname);
    }

    @PostMapping("/sign-up")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserResponseDto signUp(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        return this.userService.signUp(userId, signUpRequestDto);
    }
}
