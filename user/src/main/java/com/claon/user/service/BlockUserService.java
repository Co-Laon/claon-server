package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.domain.RequestUserInfo;
import com.claon.user.common.exception.BadRequestException;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.NotFoundException;
import com.claon.user.common.validator.NotIdEqualValidator;
import com.claon.user.domain.BlockUser;
import com.claon.user.domain.User;
import com.claon.user.dto.BlockUserFindResponseDto;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserService {
    private final UserRepository userRepository;
    private final LaonRepository laonRepository;
    private final BlockUserRepository blockUserRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public void createBlock(
            RequestUserInfo userInfo,
            String blockId
    ) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                )
        );

        User blockUser = userRepository.findById(blockId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", blockId)
                )
        );

        NotIdEqualValidator.of(user.getId(), blockUser.getId(), BlockUser.domain).validate();

        blockUserRepository.findByUserIdAndBlockId(user.getId(), blockUser.getId()).ifPresent(
                b -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 차단 관계입니다."
                    );
                }
        );

        laonRepository.findByLaonIdAndUserId(blockUser.getId(), user.getId()).ifPresent(laonRepository::delete);

        blockUserRepository.save(BlockUser.of(user, blockUser));
    }

    @Transactional
    public void deleteBlock(
            RequestUserInfo userInfo,
            String blockId
    ) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                )
        );

        User blockUser = userRepository.findById(blockId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", blockId)
                )
        );

        BlockUser blockedRelation = blockUserRepository.findByUserIdAndBlockId(user.getId(), blockUser.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "차단 관계가 아닙니다."
                )
        );

        blockUserRepository.delete(blockedRelation);
    }

    @Transactional(readOnly = true)
    public Pagination<BlockUserFindResponseDto> findBlockUser(
            RequestUserInfo userInfo,
            Pageable pageable
    ) {
        User user = userRepository.findById(userInfo.id()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", userInfo.id())
                )
        );

        return this.paginationFactory.create(
                this.blockUserRepository.findByUser(user, pageable)
                        .map(BlockUserFindResponseDto::from)
        );
    }
}
