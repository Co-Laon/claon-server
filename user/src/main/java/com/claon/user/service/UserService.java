package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.exception.BadRequestException;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.NotFoundException;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.common.validator.IsPrivateValidator;
import com.claon.user.domain.User;
import com.claon.user.dto.*;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.user.repository.UserRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRepositorySupport userRepositorySupport;
    private final LaonRepository laonRepository;
    private final BlockUserRepository blockUserRepository;
//    private final PostPort postPort;
//    private final CenterPort centerPort;
    private final PaginationFactory paginationFactory;

    @Transactional
    public PublicScopeResponseDto changePublicScope(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userId)
                )
        );

        user.changePublicScope();

        return PublicScopeResponseDto.from(userRepository.save(user).getIsPrivate());
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDto retrieveMe(String userId) {
//        List<String> postIds = this.postPort.selectPostIdsByUserId(user.getId());
//        Long postCount = (long) postIds.size();

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(userId);
        Long laonCount = (long) userIds.size();

//        List<CenterClimbingHistoryResponseDto> climbingHistories = postPort.findClimbingHistoryByPostIds(postIds);

//        return UserDetailResponseDto.from(user, postCount, laonCount, climbingHistories);
        return null;
    }

    @Transactional(readOnly = true)
    public IndividualUserResponseDto getOtherUserInformation(
            String userId,
            String userNickname
    ) {
        User targetUser = this.userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userNickname)
                ));

//        List<String> postIds = this.postPort.selectPostIdsByUserId(targetUser.getId());
//        Long postCount = (long) postIds.size();

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(targetUser.getId());
        Long laonCount = (long) userIds.size();

        boolean isLaon = userIds.contains(userId);

//        List<CenterClimbingHistoryResponseDto> climbingHistories = postPort.findClimbingHistoryByPostIds(postIds);

//        return IndividualUserResponseDto.from(targetUser, isLaon, postCount, laonCount, climbingHistories);
        return null;
    }

    @Transactional
    public Pagination<UserPostThumbnailResponseDto> findPostsByUser(
            String userId,
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
        if (!userId.equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), userId).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

//        return postPort.findPostsByUser(targetUser, pageable);
        return null;
    }

    @Transactional(readOnly = true)
    public Pagination<UserPreviewResponseDto> searchUser(
            String userId,
            String nickname,
            Pageable pageable
    ) {
        return paginationFactory.create(
                this.userRepositorySupport.searchUser(userId, nickname, pageable).map(
                        u -> UserPreviewResponseDto.from(
                                u,
                                laonRepository.findByLaonIdAndUserId(u.getId(), userId).isPresent()))
        );
    }

    @Transactional
    public UserResponseDto modifyUser(
            String userId,
            UserModifyRequestDto dto
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userId)
                )
        );

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
    public void delete(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userId)
                )
        );

        this.userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto retrieveMyAccount(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userId)
                )
        );

        return UserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<HistoryGroupByMonthDto> findHistoryByCenterIdAndUserId(
            String userId,
            String nickname,
            String centerId
    ) {
        User targetUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                )
        );

        if (!userId.equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), userId).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

//        if (!this.centerPort.existsByCenterId(centerId)) {
//            throw new NotFoundException(
//                    ErrorCode.DATA_DOES_NOT_EXIST,
//                    "암장을 찾을 수 없습니다."
//            );
//        }

//        return this.postPort.findByCenterIdAndUserId(centerId, targetUser.getId());
        return null;
    }

    @Transactional(readOnly = true)
    public Pagination<UserCenterResponseDto> findCenterHistory(
            String userId,
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
        if (!userId.equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), userId).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

//        return paginationFactory.create(
//                this.postPort.selectDistinctCenterByUser(targetUser, pageable)
//        );
        return null;
    }

    @Transactional(readOnly = true)
    public List<HistoryByDateFindResponseDto> findHistoryByDateAndUserId(String userId, String nickname, Integer year, Integer month) {

        User targetUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다", nickname)
                )
        );

        if (!userId.equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getNickname(), targetUser.getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(targetUser.getId(), userId).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", nickname)
                );
            }
        }

//        return this.postPort.findHistoryByDate(targetUser.getId(), year, month);
        return null;
    }
}
