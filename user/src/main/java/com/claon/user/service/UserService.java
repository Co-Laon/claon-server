package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.NotFoundException;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.domain.User;
import com.claon.user.dto.*;
import com.claon.user.dto.request.UserModifyRequestDto;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import com.claon.user.repository.UserRepositorySupport;
import com.claon.user.service.client.PostClient;
import com.claon.user.service.client.dto.PostThumbnailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        User requestUser = findUserById(userInfo.id());

        return UserDetailResponseDto.from(
                requestUser,
                (long) laonRepository.findUserIdsByLaonId(userInfo.id()).size(),
                postClient.findHistoriesByUserId(userInfo.id())
        );
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDto findUserById(
            RequestUserInfo userInfo,
            String targetId
    ) {
        User targetUser = findUserById(targetId);

        List<String> userIds = laonRepository.findUserIdsByLaonId(targetUser.getId());

        return UserDetailResponseDto.from(
                targetUser,
                (long) userIds.size(),
                userIds.contains(userInfo.id()),
                postClient.findHistoriesByUserId(targetUser.getId())
        );
    }

    @Transactional
    public Pagination<PostThumbnailResponse> findPostsByUser(
            RequestUserInfo userInfo,
            String targetId,
            Pageable pageable
    ) {
        User targetUser = findUserById(targetId);

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
                this.userRepositorySupport.searchUser(userInfo.id(), nickname, pageable)
                        .map(user -> UserPreviewResponseDto.from(
                                user,
                                laonRepository.findByLaonIdAndUserId(user.getId(), userInfo.id()).isPresent()))
        );
    }

    @Transactional
    public UserResponseDto modifyUser(
            RequestUserInfo userInfo,
            UserModifyRequestDto dto
    ) {
        User user = findUserById(userInfo.id());

        userRepository.findByNickname(dto.nickname())
                .ifPresent(u -> {
                    throw new UnauthorizedException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 존재하는 닉네임입니다."
                    );
                });

        user.modifyUser(
                dto.nickname(),
                Optional.ofNullable(dto.height()).orElse(0.0f),
                Optional.ofNullable(dto.armReach()).orElse(0.0f)
        );

        return UserResponseDto.from(this.userRepository.save(user));
    }

    @Transactional
    public void delete(RequestUserInfo userInfo) {
        User user = findUserById(userInfo.id());

        this.userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto retrieveMyAccount(RequestUserInfo userInfo) {
        User user = findUserById(userInfo.id());

        return UserResponseDto.from(user);
    }

    private User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", id)
                )
        );
    }
}
