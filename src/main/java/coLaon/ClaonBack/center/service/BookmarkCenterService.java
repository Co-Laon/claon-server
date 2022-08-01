package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.domain.BookmarkCenter;
import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.dto.BookmarkCenterResponseDto;
import coLaon.ClaonBack.center.repository.BookmarkCenterRepository;
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
public class BookmarkCenterService {
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final BookmarkCenterRepository bookmarkCenterRepository;

    @Transactional
    public BookmarkCenterResponseDto create(
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

        this.bookmarkCenterRepository.findByUserIdAndCenterId(user.getId(), center.getId()).ifPresent(
                bookmarkCenter -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 즐겨찾기에 등록된 암장입니다."
                    );
                }
        );

        return BookmarkCenterResponseDto.from(
                this.bookmarkCenterRepository.save(
                        BookmarkCenter.of(
                                center,
                                user
                        )
                ),
                true
        );
    }

    @Transactional
    public BookmarkCenterResponseDto delete(
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

        BookmarkCenter bookmarkCenter = this.bookmarkCenterRepository.findByUserIdAndCenterId(user.getId(), center.getId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "즐겨찾기에 등록되지 않은 암장입니다."
                )
        );

        this.bookmarkCenterRepository.delete(bookmarkCenter);

        return BookmarkCenterResponseDto.from(
                BookmarkCenter.of(
                        center,
                        user
                ),
                false
        );
    }
}
