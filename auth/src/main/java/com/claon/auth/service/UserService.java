package com.claon.auth.service;

import com.claon.auth.common.domain.JwtDto;
import com.claon.auth.common.exception.ErrorCode;
import com.claon.auth.common.exception.UnauthorizedException;
import com.claon.auth.common.utils.JwtUtil;
import com.claon.auth.domain.User;
import com.claon.auth.dto.DuplicatedCheckResponseDto;
import com.claon.auth.dto.request.SignInRequestDto;
import com.claon.auth.dto.request.SignUpRequestDto;
import com.claon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(String nickname) {
        return DuplicatedCheckResponseDto.of(this.userRepository.findByNickname(nickname).isPresent());
    }

    @Transactional
    public JwtDto signIn(
            SignInRequestDto signInRequestDto
    ) {
        User user = this.userRepository.findByEmail(signInRequestDto.email())
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                ));

        return this.jwtUtil.createToken(user.getId());
    }

    @Transactional
    public JwtDto signUp(
            SignUpRequestDto signUpRequestDto
    ) {
        User user = User.signUp(
                signUpRequestDto.email(),
                signUpRequestDto.nickname(),
                signUpRequestDto.height() == null ? 0 : signUpRequestDto.height(),
                signUpRequestDto.armReach() == null ? 0 : signUpRequestDto.armReach()
        );

        return this.jwtUtil.createToken(userRepository.save(user).getId());
    }

    public JwtDto reissue(String refreshToken) {
        return this.jwtUtil.reissueToken(refreshToken);
    }

    public void signOut(JwtDto jwtDto) {
        this.jwtUtil.deleteRefreshToken(jwtDto.getRefreshToken());
    }
}
