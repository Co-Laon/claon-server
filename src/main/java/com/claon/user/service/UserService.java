package com.claon.user.service;

import com.claon.common.domain.JwtDto;
import com.claon.common.domain.Pagination;
import com.claon.common.domain.PaginationFactory;
import com.claon.common.exception.BadRequestException;
import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.NotFoundException;
import com.claon.common.exception.UnauthorizedException;
import com.claon.common.utils.JwtUtil;
import com.claon.common.validator.IsImageValidator;
import com.claon.common.validator.IsPrivateValidator;
import com.claon.user.domain.User;
import com.claon.user.domain.enums.OAuth2Provider;
import com.claon.user.dto.DuplicatedCheckResponseDto;
import com.claon.user.dto.HistoryByDateFindResponseDto;
import com.claon.user.dto.HistoryGroupByMonthDto;
import com.claon.user.dto.IndividualUserResponseDto;
import com.claon.user.dto.InstagramResponseDto;
import com.claon.user.dto.PublicScopeResponseDto;
import com.claon.user.dto.SignInRequestDto;
import com.claon.user.dto.SignUpRequestDto;
import com.claon.user.dto.UserCenterResponseDto;
import com.claon.user.dto.UserDetailResponseDto;
import com.claon.user.dto.UserModifyRequestDto;
import com.claon.user.dto.UserPreviewResponseDto;
import com.claon.user.dto.UserResponseDto;
import com.claon.user.infra.InstagramUserInfoProvider;
import com.claon.user.infra.ProfileImageManager;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.user.repository.UserRepositorySupport;
import com.claon.user.dto.CenterClimbingHistoryResponseDto;
import com.claon.user.dto.OAuth2UserInfoDto;
import com.claon.user.dto.UserPostThumbnailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRepositorySupport userRepositorySupport;
    private final LaonRepository laonRepository;
    private final BlockUserRepository blockUserRepository;
    private final PostPort postPort;
    private final CenterPort centerPort;
    private final OAuth2UserInfoProviderSupplier oAuth2UserInfoProviderSupplier;
    private final InstagramUserInfoProvider instagramUserInfoProvider;
    private final JwtUtil jwtUtil;
    private final ProfileImageManager profileImageManager;
    private final PaginationFactory paginationFactory;

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

    @Transactional
    public PublicScopeResponseDto changePublicScope(User user) {
        user.changePublicScope();

        return PublicScopeResponseDto.from(userRepository.save(user).getIsPrivate());
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDto retrieveMe(User user) {
        List<String> postIds = this.postPort.selectPostIdsByUserId(user.getId());
        Long postCount = (long) postIds.size();

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(user.getId());
        Long laonCount = (long) userIds.size();

        List<CenterClimbingHistoryResponseDto> climbingHistories = postPort.findClimbingHistoryByPostIds(postIds);

        return UserDetailResponseDto.from(user, postCount, laonCount, climbingHistories);
    }

    @Transactional(readOnly = true)
    public IndividualUserResponseDto getOtherUserInformation(
            User user,
            String userNickname
    ) {
        User targetUser = this.userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userNickname)
                ));

        List<String> postIds = this.postPort.selectPostIdsByUserId(targetUser.getId());
        Long postCount = (long) postIds.size();

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(targetUser.getId());
        Long laonCount = (long) userIds.size();

        boolean isLaon = userIds.contains(user.getId());

        List<CenterClimbingHistoryResponseDto> climbingHistories = postPort.findClimbingHistoryByPostIds(postIds);

        return IndividualUserResponseDto.from(targetUser, isLaon, postCount, laonCount, climbingHistories);
    }

    @Transactional
    public Pagination<UserPostThumbnailResponseDto> findPostsByUser(
            User user,
            String nickname,
            Pageable pageable
    ) {
        User targetUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                )
        );

        // individual user page
        if (!user.getId().equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), user.getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

        return postPort.findPostsByUser(targetUser, pageable);
    }

    @Transactional(readOnly = true)
    public Pagination<UserPreviewResponseDto> searchUser(
            User user,
            String nickname,
            Pageable pageable
    ) {
        return paginationFactory.create(
                this.userRepositorySupport.searchUser(user.getId(), nickname, pageable).map(
                        u -> UserPreviewResponseDto.from(
                                u,
                                laonRepository.findByLaonIdAndUserId(u.getId(), user.getId()).isPresent()))
        );
    }

    @Transactional
    public UserResponseDto modifyUser(
            User user,
            UserModifyRequestDto dto
    ) {
        user.modifyUser(
                dto.getNickname(),
                dto.getHeight().orElse(0.0f),
                dto.getArmReach().orElse(0.0f),
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

    @Transactional(readOnly = true)
    public UserResponseDto retrieveMyAccount(User user) {
        return UserResponseDto.from(user);
    }

    public String uploadProfile(MultipartFile image) {
        IsImageValidator.of(image).validate();

        return this.profileImageManager.uploadProfile(image);
    }

    public void deleteProfile(User user) {
        if (user.getImagePath().isEmpty()) {
            throw new NotFoundException(
                    ErrorCode.DATA_DOES_NOT_EXIST,
                    "프로필 이미지를 찾을 수 없습니다."
            );
        }

        this.profileImageManager.deleteProfile(user.getImagePath());
    }

    @Transactional(readOnly = true)
    public List<HistoryGroupByMonthDto> findHistoryByCenterIdAndUserId(
            User user,
            String nickname,
            String centerId
    ) {
        User targetUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                )
        );

        if (!user.getId().equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), user.getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

        if (!this.centerPort.existsByCenterId(centerId)) {
            throw new NotFoundException(
                    ErrorCode.DATA_DOES_NOT_EXIST,
                    "암장을 찾을 수 없습니다."
            );
        }

        return this.postPort.findByCenterIdAndUserId(centerId, targetUser.getId());
    }

    @Transactional(readOnly = true)
    public Pagination<UserCenterResponseDto> findCenterHistory(
            User user,
            String nickname,
            Pageable pageable
    ) {
        User targetUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                )
        );

        // individual user page
        if (!user.getId().equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), user.getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

        return paginationFactory.create(
                this.postPort.selectDistinctCenterByUser(targetUser, pageable)
        );
    }

    @Transactional(readOnly = true)
    public List<HistoryByDateFindResponseDto> findHistoryByDateAndUserId(User user, String nickname, Integer year, Integer month) {

        User targetUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다", nickname)
                )
        );

        if (!user.getId().equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), user.getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

        return this.postPort.findHistoryByDate(targetUser.getId(), year, month);
    }
}
