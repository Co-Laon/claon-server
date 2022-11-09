package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.CenterBookmark;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.dto.CenterBookmarkResponseDto;
import coLaon.ClaonBack.center.repository.CenterBookmarkRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CenterBookmarkService {
    private final CenterRepository centerRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;

    @Transactional
    public CenterBookmarkResponseDto create(
            User user,
            String centerId
    ) {
        Center center = this.centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        this.centerBookmarkRepository.findByUserIdAndCenterId(user.getId(), center.getId()).ifPresent(
                bookmarkCenter -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 즐겨찾기에 등록되어 있습니다."
                    );
                }
        );

        return CenterBookmarkResponseDto.from(
                this.centerBookmarkRepository.save(
                        CenterBookmark.of(
                                center,
                                user
                        )
                ),
                true
        );
    }

    @Transactional
    public CenterBookmarkResponseDto delete(
            User user,
            String centerId
    ) {
        Center center = this.centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        CenterBookmark bookmarkCenter = this.centerBookmarkRepository.findByUserIdAndCenterId(user.getId(), center.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "아직 즐겨찾기에 등록되지 않았습니다."
                )
        );

        this.centerBookmarkRepository.delete(bookmarkCenter);

        return CenterBookmarkResponseDto.from(
                CenterBookmark.of(
                        center,
                        user
                ),
                false
        );
    }
}
