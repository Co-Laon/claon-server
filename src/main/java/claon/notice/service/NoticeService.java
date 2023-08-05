package claon.notice.service;

import claon.common.domain.Pagination;
import claon.common.domain.PaginationFactory;
import claon.common.validator.IsAdminValidator;
import claon.user.domain.User;
import claon.notice.domain.Notice;
import claon.notice.dto.NoticeCreateRequestDto;
import claon.notice.dto.NoticeResponseDto;
import claon.notice.repository.NoticeRepository;
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
