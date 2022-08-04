package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.BlockUser;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.BlockUserFindResponseDto;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
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
    public void createBlock(String userId, String blockNickname) {
        User blockUser = userRepository.findByNickname(blockNickname).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", blockNickname)
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        laonRepository.findByLaonIdAndUserId(blockUser.getId(), user.getId()).ifPresent(laonRepository::delete);

        blockUserRepository.findByUserIdAndBlockId(user.getId(), blockUser.getId()).ifPresent(
                b -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 차단 관계입니다."
                    );
                }
        );

        blockUserRepository.save(BlockUser.of(user, blockUser));
    }

    @Transactional
    public void deleteBlock(String userId, String blockNickname) {
        User blockUser = userRepository.findByNickname(blockNickname).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", blockNickname)
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
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
    public Pagination<BlockUserFindResponseDto> findBlockUser(String userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        return this.paginationFactory.create(
                this.blockUserRepository.findByUserId(user.getId(), pageable)
                        .map(BlockUserFindResponseDto::from)
        );
    }
}
