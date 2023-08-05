package claon.user.service;

import claon.common.domain.Pagination;
import claon.common.domain.PaginationFactory;
import claon.common.exception.BadRequestException;
import claon.common.exception.ErrorCode;
import claon.common.exception.NotFoundException;
import claon.common.exception.UnauthorizedException;
import claon.common.validator.NotIdEqualValidator;
import claon.user.domain.Laon;
import claon.user.domain.User;
import claon.user.dto.LaonFindResponseDto;
import claon.user.dto.UserPostDetailResponseDto;
import claon.user.repository.BlockUserRepository;
import claon.user.repository.LaonRepository;
import claon.user.repository.LaonRepositorySupport;
import claon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LaonService {
    private final UserRepository userRepository;
    private final LaonRepository laonRepository;
    private final BlockUserRepository blockUserRepository;
    private final LaonRepositorySupport laonRepositorySupport;
    private final PaginationFactory paginationFactory;
    private final PostPort postPort;

    @Transactional
    public void createLaon(User user, String laonNickname) {
        User laon = userRepository.findByNickname(laonNickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", laonNickname)
                )
        );

        NotIdEqualValidator.of(user.getId(), laon.getId(), Laon.domain).validate();

        if (!blockUserRepository.findBlock(user.getId(), laon.getId()).isEmpty()) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_ACCESSIBLE,
                    String.format("%s을 찾을 수 없습니다.", laonNickname)
            );
        }

        laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId()).ifPresent(
                l -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            String.format("%s을 이미 라온했습니다.", laonNickname)
                    );
                }
        );

        laonRepository.save(Laon.of(user, laon));
    }

    @Transactional
    public void deleteLaon(User user, String laonNickname) {
        User laon = userRepository.findByNickname(laonNickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", laonNickname)
                )
        );

        Laon laonRelation = laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s을 아직 라온하지 않았습니다.", laonNickname)
                )
        );

        laonRepository.delete(laonRelation);
    }

    @Transactional(readOnly = true)
    public Pagination<LaonFindResponseDto> findAllLaon(User user, Pageable pageable) {
        return this.paginationFactory.create(
                laonRepositorySupport.findAllByUserId(user.getId(), pageable)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<UserPostDetailResponseDto> findLaonPost(
            User user,
            Pageable pageable
    ) {
        return postPort.findLaonPost(user, pageable);
    }
}
