package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.LaonFindResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
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
    private final PaginationFactory paginationFactory;

    @Transactional
    public void createLaon(String laonNickname, String userId) {
        User laon = userRepository.findByNickname(laonNickname).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", laonNickname)
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        laonRepository.findByLaonIdAndUserId(laon.getId(), user.getId()).ifPresent(
                l -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 라온 관계입니다."
                    );
                }
        );

        laonRepository.save(Laon.of(laon, user));
    }

    @Transactional
    public void deleteLaon(String laonNickname, String userId) {
        User laon = userRepository.findByNickname(laonNickname).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", laonNickname)
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
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
    public Pagination<LaonFindResponseDto> findAllLaon(String userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        return this.paginationFactory.create(
                laonRepository.findAllByUserId(userId, pageable)
                        .map(laon -> LaonFindResponseDto.from(laon))
        );
    }
}
