package com.claon.auth.service;

import com.claon.auth.common.domain.JwtDto;
import com.claon.auth.common.exception.BadRequestException;
import com.claon.auth.common.exception.ErrorCode;
import com.claon.auth.common.exception.UnauthorizedException;
import com.claon.auth.common.utils.JwtUtil;
import com.claon.auth.domain.User;
import com.claon.auth.domain.enums.OAuth2Provider;
import com.claon.auth.dto.DuplicatedCheckResponseDto;
import com.claon.auth.dto.OAuth2UserInfoDto;
import com.claon.auth.dto.SignInRequestDto;
import com.claon.auth.dto.SignUpRequestDto;
import com.claon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OAuth2UserInfoProviderSupplier oAuth2UserInfoProviderSupplier;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(String nickname) {
        return DuplicatedCheckResponseDto.of(this.userRepository.findByNickname(nickname).isPresent());
    }

    @Transactional
    public JwtDto signIn(
            OAuth2Provider provider,
            SignInRequestDto signInRequestDto
    ) {
        OAuth2UserInfoDto userInfoDto = this.oAuth2UserInfoProviderSupplier.getProvider(provider)
                .getUserInfo(signInRequestDto.getCode());

        User user = this.userRepository.findByEmailAndOAuthId(userInfoDto.getEmail(), userInfoDto.getOAuthId())
                .orElseThrow(() -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                ));

        return this.jwtUtil.createToken(user.getId());
    }

    @Transactional
    public JwtDto signUp(
            OAuth2Provider provider,
            SignUpRequestDto signUpRequestDto
    ) {
        Optional.ofNullable(signUpRequestDto.getInstagramOAuthId())
                .flatMap(this.userRepository::findByInstagramOAuthId).ifPresent(
                        u -> {
                            throw new BadRequestException(
                                    ErrorCode.ROW_ALREADY_EXIST,
                                    "이미 가입한 인스타그램 계정입니다."
                            );
                        }
                );

        OAuth2UserInfoDto userInfoDto = this.oAuth2UserInfoProviderSupplier.getProvider(provider)
                .getUserInfo(signUpRequestDto.getCode());

        User user = User.signUp(
                userInfoDto.getEmail(),
                userInfoDto.getOAuthId(),
                signUpRequestDto.getNickname(),
                signUpRequestDto.getHeight() == null ? 0 : signUpRequestDto.getHeight(),
                signUpRequestDto.getArmReach() == null ? 0 : signUpRequestDto.getArmReach(),
                signUpRequestDto.getImagePath(),
                signUpRequestDto.getInstagramOAuthId(),
                signUpRequestDto.getInstagramUserName()
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
