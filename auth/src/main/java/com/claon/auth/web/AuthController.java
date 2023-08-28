package com.claon.auth.web;

import com.claon.auth.common.domain.JwtDto;
import com.claon.auth.common.utils.HeaderUtil;
import com.claon.auth.dto.DuplicatedCheckResponseDto;
import com.claon.auth.dto.SignInRequestDto;
import com.claon.auth.dto.SignUpRequestDto;
import com.claon.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/sign-in")
    @ResponseStatus(value = HttpStatus.OK)
    public JwtDto signIn(
            HttpServletResponse response,
            @RequestBody @Valid SignInRequestDto signInRequestDto
    ) {
        JwtDto jwtDto = this.userService.signIn(signInRequestDto);

        this.headerUtil.addToken(response, jwtDto);
        return jwtDto;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(value = HttpStatus.CREATED)
    public JwtDto signUp(
            HttpServletResponse response,
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        JwtDto jwtDto = this.userService.signUp(signUpRequestDto);

        this.headerUtil.addToken(response, jwtDto);
        return jwtDto;
    }

    @PostMapping("/reissue")
    @ResponseStatus(value = HttpStatus.OK)
    public JwtDto reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = this.headerUtil.resolveRefreshToken(request);

        JwtDto newJwtDto = this.userService.reissue(refreshToken);

        this.headerUtil.addToken(response, newJwtDto);
        return newJwtDto;
    }

    @PostMapping("/sign-out")
    @ResponseStatus(value = HttpStatus.OK)
    public void signOut(
            HttpServletRequest request
    ) {
        this.userService.signOut(JwtDto.of(
                this.headerUtil.resolveAccessToken(request),
                this.headerUtil.resolveRefreshToken(request)
        ));
    }
}
