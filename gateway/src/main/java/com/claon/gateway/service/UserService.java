package com.claon.gateway.service;

import com.claon.gateway.common.domain.JwtDto;
import com.claon.gateway.common.exception.BadRequestException;
import com.claon.gateway.common.exception.ErrorCode;
import com.claon.gateway.common.utils.JwtUtil;
import com.claon.gateway.domain.User;
import com.claon.gateway.domain.enums.OAuth2Provider;
import com.claon.gateway.dto.*;
import com.claon.gateway.repository.UserRepository;
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
                .orElseGet(() -> this.userRepository.save(User.createNewUser(userInfoDto.getEmail(), userInfoDto.getOAuthId())));

        return this.jwtUtil.createToken(
                user.getId(),
                user.isSignupCompleted()
        );
    }

    @Transactional
    public UserResponseDto signUp(
            User user,
            SignUpRequestDto signUpRequestDto
    ) {
        Optional.ofNullable(signUpRequestDto.getInstagramOAuthId()).flatMap(
                this.userRepository::findByInstagramOAuthId).ifPresent(
                u -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 가입한 인스타그램 계정입니다."
                    );
                }
        );

        user.signUp(
                signUpRequestDto.getNickname(),
                signUpRequestDto.getHeight() == null ? 0 : signUpRequestDto.getHeight(),
                signUpRequestDto.getArmReach() == null ? 0 : signUpRequestDto.getArmReach(),
                signUpRequestDto.getImagePath(),
                signUpRequestDto.getInstagramOAuthId(),
                signUpRequestDto.getInstagramUserName()
        );

        return UserResponseDto.from(userRepository.save(user));
    }

    public void signOut(JwtDto jwtDto) {
        this.jwtUtil.deleteRefreshToken(jwtDto.getRefreshToken());
    }
}
