package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.enums.BasicLocalArea;
import coLaon.ClaonBack.common.domain.enums.MetropolitanArea;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.utils.JwtUtil;
import coLaon.ClaonBack.config.dto.JwtDto;
import coLaon.ClaonBack.user.domain.OAuth2Provider;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.InstagramResponseDto;
import coLaon.ClaonBack.user.dto.OAuth2UserInfoDto;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.SignInRequestDto;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.infra.InstagramUserInfoProvider;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OAuth2UserInfoProviderSupplier oAuth2UserInfoProviderSupplier;
    private final InstagramUserInfoProvider instagramUserInfoProvider;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public DuplicatedCheckResponseDto nicknameDuplicatedCheck(String nickname) {
        return DuplicatedCheckResponseDto.of(this.userRepository.findByNickname(nickname).isPresent());
    }

    @Transactional(readOnly = true)
    public InstagramResponseDto getInstagramAccount(SignInRequestDto requestDto) {
        OAuth2UserInfoDto userInfoDto = this.instagramUserInfoProvider.getUserInfo(requestDto.getCode());

        return InstagramResponseDto.of(
                userInfoDto.getOAuthId(),
                userInfoDto.getEmail()
        );
    }

    @Transactional
    public JwtDto signIn(
            String provider,
            SignInRequestDto signInRequestDto
    ) {
        OAuth2UserInfoDto userInfoDto = this.oAuth2UserInfoProviderSupplier.getProvider(OAuth2Provider.of(provider))
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
            String userId,
            SignUpRequestDto signUpRequestDto
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> {
                    throw new UnauthorizedException(
                            ErrorCode.USER_DOES_NOT_EXIST,
                            "???????????? ?????? ??? ????????????."
                    );
                });

        this.userRepository.findByNickname(signUpRequestDto.getNickname()).ifPresent(
                u -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "?????? ???????????? ??????????????????."
                    );
                }
        );

        this.userRepository.findByInstagramOAuthId(signUpRequestDto.getInstagramOAuthId()).ifPresent(
                u -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "?????? ????????? ??????????????? ???????????????."
                    );
                }
        );

        user.signUp(
                signUpRequestDto.getNickname(),
                MetropolitanArea.of(signUpRequestDto.getMetropolitanActiveArea()),
                BasicLocalArea.of(
                        signUpRequestDto.getMetropolitanActiveArea(),
                        signUpRequestDto.getBasicLocalActiveArea()
                ),
                signUpRequestDto.getImagePath(),
                signUpRequestDto.getInstagramOAuthId(),
                signUpRequestDto.getInstagramUserName()
        );

        return UserResponseDto.from(userRepository.save(user));
    }

    @Transactional
    public PublicScopeResponseDto setPublicScope(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "???????????? ?????? ??? ????????????."
                )
        );

        user.changePublicScope();
        return PublicScopeResponseDto.from(userRepository.save(user).getIsPrivate());
    }

    public UserResponseDto getUser(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UnauthorizedException(
                    ErrorCode.USER_DOES_NOT_EXIST,
                    "???????????? ???????????? ?????? ????????? ???????????? ????????????."
            );
        });
        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto modifyUser(String userId, UserModifyRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UnauthorizedException(
                    ErrorCode.USER_DOES_NOT_EXIST,
                    "???????????? ???????????? ?????? ????????? ???????????? ????????????."
            );
        });

        user.modifyUser(dto);
        return UserResponseDto.from(user);
    }
}


