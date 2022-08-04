package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.CenterBookmark;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.dto.CenterBookmarkResponseDto;
import coLaon.ClaonBack.center.repository.CenterBookmarkRepository;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CenterBookmarkService {
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;

    @Transactional
    public CenterBookmarkResponseDto create(
            String userId,
            String centerId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = this.centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        this.centerBookmarkRepository.findByUserIdAndCenterId(user.getId(), center.getId()).ifPresent(
                bookmarkCenter -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 즐겨찾기에 등록된 암장입니다."
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
            String userId,
            String centerId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = this.centerRepository.findById(centerId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        CenterBookmark bookmarkCenter = this.centerBookmarkRepository.findByUserIdAndCenterId(user.getId(), center.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "즐겨찾기에 등록되지 않은 암장입니다."
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
