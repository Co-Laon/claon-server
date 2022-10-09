package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.validator.NotIdEqualValidator;
import coLaon.ClaonBack.user.dto.PostDetailResponseDto;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.LaonFindResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.LaonRepositorySupport;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LaonService {
    private final UserRepository userRepository;
    private final LaonRepository laonRepository;
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

        laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId()).ifPresent(
                l -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 라온 관계입니다."
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
                        "라온 관계가 아닙니다."
                )
        );

        laonRepository.delete(laonRelation);
    }

    @Transactional(readOnly = true)
    public Pagination<LaonFindResponseDto> findAllLaon(User user, Pageable pageable) {
        return this.paginationFactory.create(
                laonRepositorySupport.findAllByUserId(user.getId(), pageable)
                        .map(LaonFindResponseDto::from)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findLaonPost(
            User user,
            Pageable pageable
    ) {
        return postPort.findLaonPost(user, pageable);
    }
}
