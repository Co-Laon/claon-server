package coLaon.ClaonBack.user.web;

import coLaon.ClaonBack.common.utils.CookieUtil;
import coLaon.ClaonBack.config.dto.JwtDto;
import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.InstagramResponseDto;
import coLaon.ClaonBack.user.dto.SignInRequestDto;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final CookieUtil cookieUtil;

    @GetMapping("/nickname/{nickname}/duplicate-check")
    @ResponseStatus(value = HttpStatus.OK)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(@PathVariable String nickname) {
        return this.userService.nicknameDuplicatedCheck(nickname);
    }

    @PostMapping("/instagram/account")
    @ResponseStatus(value = HttpStatus.OK)
    public InstagramResponseDto getInstagramAccount(
            @RequestBody SignInRequestDto signInRequestDto
    ) {
        return this.userService.getInstagramAccount(signInRequestDto);
    }

    @PostMapping("/sign-in/{provider}")
    @ResponseStatus(value = HttpStatus.OK)
    public JwtDto signIn(
            HttpServletResponse response,
            @PathVariable String provider,
            @RequestBody SignInRequestDto signInRequestDto
    ) {
        JwtDto jwtDto = this.userService.signIn(provider, signInRequestDto);

        this.cookieUtil.addToken(response, jwtDto);
        return jwtDto;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserResponseDto signUp(
            HttpServletResponse response,
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        UserResponseDto userResponseDto = this.userService.signUp(userId, signUpRequestDto);

        this.cookieUtil.createCookie(response, "isCompletedSignUp", Boolean.TRUE.toString());
        return userResponseDto;
    }
}
