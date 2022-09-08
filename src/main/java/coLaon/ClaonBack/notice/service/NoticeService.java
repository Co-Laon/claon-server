package coLaon.ClaonBack.notice.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.validator.IsAdminValidator;
import coLaon.ClaonBack.notice.domain.Notice;
import coLaon.ClaonBack.notice.dto.NoticeCreateRequestDto;
import coLaon.ClaonBack.notice.dto.NoticeResponseDto;
import coLaon.ClaonBack.notice.repository.NoticeRepository;
import coLaon.ClaonBack.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final PaginationFactory paginationFactory;
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public Pagination<NoticeResponseDto> getNoticeList(Pageable pageable) {
        return this.paginationFactory.create(
                noticeRepository.findAllWithPagination(pageable).map(NoticeResponseDto::from)
        );
    }

    @Transactional
    public NoticeResponseDto createNotice(
            User user, NoticeCreateRequestDto dto
    ) {
        IsAdminValidator.of(user.getEmail()).validate();

        return NoticeResponseDto.from(
                noticeRepository.save(
                        Notice.of(
                                dto.getTitle(),
                                dto.getContent(),
                                user
                        )
                )
        );
    }

}
