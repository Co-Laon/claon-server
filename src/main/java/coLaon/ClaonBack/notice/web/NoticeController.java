package coLaon.ClaonBack.notice.web;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.config.UserAccount;
import coLaon.ClaonBack.notice.dto.NoticeCreateRequestDto;
import coLaon.ClaonBack.notice.dto.NoticeResponseDto;
import coLaon.ClaonBack.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public Pagination<NoticeResponseDto> getNoticeList(
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) @PageableDefault(size = 20) Pageable pageable
    ) {
        return noticeService.getNoticeList(pageable);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public NoticeResponseDto createNotice(
            @AuthenticationPrincipal UserAccount userAccount,
            @RequestBody NoticeCreateRequestDto dto
    ) {
        return noticeService.createNotice(userAccount.getUser(), dto);
    }
}
