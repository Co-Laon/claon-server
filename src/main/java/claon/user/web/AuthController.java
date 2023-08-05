package claon.user.web;

import claon.common.domain.JwtDto;
import claon.common.utils.HeaderUtil;
import claon.user.domain.UserDetails;
import claon.user.domain.enums.OAuth2Provider;
import claon.user.dto.DuplicatedCheckResponseDto;
import claon.user.dto.InstagramResponseDto;
import claon.user.dto.SignInRequestDto;
import claon.user.dto.SignUpRequestDto;
import claon.user.dto.UserResponseDto;
import claon.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final HeaderUtil headerUtil;

    @GetMapping("/nickname/{nickname}/duplicate-check")
    @ResponseStatus(value = HttpStatus.OK)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(@PathVariable String nickname) {
        return this.userService.nicknameDuplicatedCheck(nickname);
    }

    @PostMapping("/instagram/account")
    @ResponseStatus(value = HttpStatus.OK)
    public InstagramResponseDto getInstagramAccount(
            @RequestBody @Valid SignInRequestDto signInRequestDto
    ) {
        return this.userService.getInstagramAccount(signInRequestDto);
    }

    @PostMapping("/sign-in/{provider}")
    @ResponseStatus(value = HttpStatus.OK)
    public JwtDto signIn(
            HttpServletResponse response,
            @PathVariable OAuth2Provider provider,
            @RequestBody @Valid SignInRequestDto signInRequestDto
    ) {
        JwtDto jwtDto = this.userService.signIn(provider, signInRequestDto);

        this.headerUtil.addToken(response, jwtDto);
        return jwtDto;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserResponseDto signUp(
            HttpServletResponse response,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        UserResponseDto userResponseDto = this.userService.signUp(userDetails.getUser(), signUpRequestDto);

        this.headerUtil.createHeader(response, "isCompletedSignUp", Boolean.TRUE.toString());
        return userResponseDto;
    }

    @PostMapping("/sign-out")
    @ResponseStatus(value = HttpStatus.OK)
    public void signOut(
            HttpServletRequest request
    ) {
        this.userService.signOut(this.headerUtil.resolveToken(request));
    }
}
