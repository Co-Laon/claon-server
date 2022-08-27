package coLaon.ClaonBack.notice.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.IsAdminValidator;
import coLaon.ClaonBack.notice.domain.Notice;
import coLaon.ClaonBack.notice.dto.NoticeCreateRequestDto;
import coLaon.ClaonBack.notice.dto.NoticeResponseDto;
import coLaon.ClaonBack.notice.repository.NoticeRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final PaginationFactory paginationFactory;
    private final NoticeRepository noticeRepository;

    private final UserRepository userRepository;

    public Pagination<NoticeResponseDto> getNoticeList(Pageable pageable) {
        return this.paginationFactory.create(
                noticeRepository.findAllWithPagination(pageable).map(NoticeResponseDto::from)
        );
    }

    public NoticeResponseDto createNotice(String userId, NoticeCreateRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        IsAdminValidator.of(user.getEmail()).validate();

        return NoticeResponseDto.from(noticeRepository.save(Notice.of(dto.getTitle(), dto.getContent(), user)));
    }

}
