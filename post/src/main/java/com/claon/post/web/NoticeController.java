package com.claon.post.web;

import com.claon.post.common.domain.Pagination;
import com.claon.post.dto.NoticeCreateRequestDto;
import com.claon.post.dto.NoticeResponseDto;
import com.claon.post.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public Pagination<NoticeResponseDto> getNoticeList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return noticeService.getNoticeList(pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public NoticeResponseDto createNotice(
            @RequestHeader(value = "X-USER-ID") String userId,
            @RequestBody @Valid NoticeCreateRequestDto dto
    ) {
        return noticeService.createNotice(userId, dto);
    }
}
