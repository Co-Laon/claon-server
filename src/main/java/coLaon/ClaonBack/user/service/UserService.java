package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.utils.JwtUtil;
import coLaon.ClaonBack.common.domain.JwtDto;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.enums.OAuth2Provider;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.DuplicatedCheckResponseDto;
import coLaon.ClaonBack.user.dto.InstagramResponseDto;
import coLaon.ClaonBack.user.dto.OAuth2UserInfoDto;
import coLaon.ClaonBack.user.dto.PublicScopeResponseDto;
import coLaon.ClaonBack.user.dto.SignInRequestDto;
import coLaon.ClaonBack.user.dto.SignUpRequestDto;
import coLaon.ClaonBack.user.dto.UserResponseDto;
import coLaon.ClaonBack.user.dto.UserModifyRequestDto;
import coLaon.ClaonBack.user.dto.IndividualUserResponseDto;
import coLaon.ClaonBack.user.infra.InstagramUserInfoProvider;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final LaonRepository laonRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
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
                signUpRequestDto.getHeight(),
                signUpRequestDto.getArmReach(),
                signUpRequestDto.getImagePath(),
                signUpRequestDto.getInstagramOAuthId(),
                signUpRequestDto.getInstagramUserName()
        );

        return UserResponseDto.from(userRepository.save(user));
    }

    @Transactional
    public PublicScopeResponseDto changePublicScope(User user) {
        user.changePublicScope();

        return PublicScopeResponseDto.from(userRepository.save(user).getIsPrivate());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(User user) {
        return UserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public IndividualUserResponseDto getOtherUserInformation(
            User user,
            String userNickname
    ) {
        User targetUser = this.userRepository.findByNickname(userNickname).orElseThrow(() -> {
            throw new NotFoundException(
                    ErrorCode.DATA_DOES_NOT_EXIST,
                    String.format("%s을 찾을 수 없습니다.", userNickname)
            );
        });

        List<String> postIds = this.postRepository.selectPostIdsByUserId(targetUser.getId());
        Long postCount = (long) postIds.size();
        Long postLikeCount = this.postLikeRepository.countByPostIdIn(postIds);

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(targetUser.getId());
        Long laonCount = (long) userIds.size();

        boolean isLaon = userIds.contains(user.getId());

        List<ClimbingHistory> climbingHistories = climbingHistoryRepository.findByPostIds(postIds);
        return IndividualUserResponseDto.from(targetUser, isLaon, postCount, laonCount, postLikeCount, climbingHistories);
    }

    @Transactional
    public UserResponseDto modifyUser(
            User user,
            UserModifyRequestDto dto
    ) {
        user.modifyUser(
                dto.getNickname(),
                dto.getHeight(),
                dto.getArmReach(),
                dto.getImagePath(),
                dto.getInstagramUserName(),
                dto.getInstagramOAuthId()
        );

        return UserResponseDto.from(this.userRepository.save(user));
    }

    @Transactional
    public void delete(User user) {
        this.userRepository.delete(user);
    }
}


