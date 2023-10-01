package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.domain.Notice;
import com.claon.post.dto.request.NoticeRequestDto;
import com.claon.post.dto.NoticeResponseDto;
import com.claon.post.repository.NoticeRepository;
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
            RequestUserInfo userInfo, NoticeRequestDto dto
    ) {
        return NoticeResponseDto.from(
                noticeRepository.save(
                        Notice.of(
                                dto.title(),
                                dto.content(),
                                userInfo.id()
                        )
                )
        );
    }

}
