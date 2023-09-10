package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.NotFoundException;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.domain.User;
import com.claon.user.dto.*;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.user.repository.UserRepositorySupport;
import com.claon.user.service.client.PostClient;
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
    private final PostClient postClient;
    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public UserDetailResponseDto retrieveMe(RequestUserInfo userInfo) {
        User requestUser = userRepository.findById(userInfo.id())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                ));

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(userInfo.id());
        Long laonCount = (long) userIds.size();

        List<CenterClimbingHistoryResponseDto> climbingHistories = postClient.findHistoriesByUserId(userInfo.id());

        return UserDetailResponseDto.from(requestUser, laonCount, climbingHistories);
    }

    @Transactional(readOnly = true)
    public IndividualUserResponseDto getOtherUserInformation(
            RequestUserInfo userInfo,
            String targetId
    ) {
        User targetUser = this.userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", targetId)
                ));

        List<String> userIds = this.laonRepository.getUserIdsByLaonId(targetUser.getId());
        Long laonCount = (long) userIds.size();

        boolean isLaon = userIds.contains(userInfo.id());

        List<CenterClimbingHistoryResponseDto> climbingHistories = postClient.findHistoriesByUserId(targetUser.getId());

        return IndividualUserResponseDto.from(targetUser, isLaon, laonCount, climbingHistories);
    }

    @Transactional
    public Pagination<UserPostThumbnailResponseDto> findPostsByUser(
            RequestUserInfo userInfo,
            String targetId,
            Pageable pageable
    ) {
        User targetUser = userRepository.findById(targetId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", targetId)
                )
        );

        // individual user page
        if (!userInfo.id().equals(targetUser.getId())) {
            if (!blockUserRepository.findBlock(targetUser.getId(), userInfo.id()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", targetId)
                );
            }
        }

        return postClient.findPostThumbnails(targetUser.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Pagination<UserPreviewResponseDto> searchUser(
            RequestUserInfo userInfo,
            String nickname,
            Pageable pageable
    ) {
        return paginationFactory.create(
                this.userRepositorySupport.searchUser(userInfo.id(), nickname, pageable).map(
                        u -> UserPreviewResponseDto.from(
                                u,
                                laonRepository.findByLaonIdAndUserId(u.getId(), userInfo.id()).isPresent()))
        );
    }

    @Transactional
    public UserResponseDto modifyUser(
            RequestUserInfo userInfo,
            UserModifyRequestDto dto
    ) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                )
        );

        user.modifyUser(
                dto.getNickname(),
                dto.getHeight().orElse(0.0f),
                dto.getArmReach().orElse(0.0f)
        );

        return UserResponseDto.from(this.userRepository.save(user));
    }

    @Transactional
    public void delete(RequestUserInfo userInfo) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                )
        );

        this.userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto retrieveMyAccount(RequestUserInfo userInfo) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                )
        );

        return UserResponseDto.from(user);
    }
}
