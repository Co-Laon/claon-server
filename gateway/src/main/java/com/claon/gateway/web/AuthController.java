package com.claon.gateway.web;

import com.claon.gateway.common.domain.JwtDto;
import com.claon.gateway.common.utils.HeaderUtil;
import com.claon.gateway.domain.UserDetails;
import com.claon.gateway.domain.enums.OAuth2Provider;
import com.claon.gateway.dto.*;
import com.claon.gateway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
