package com.claon.post.dto;

import com.claon.post.common.utils.RelativeTimeUtil;
import com.claon.post.domain.Notice;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@ToString
public class NoticeResponseDto {
    private final String title;
    private final String content;
    private final String createdAt;

    private NoticeResponseDto(String title, String content, String createdAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static NoticeResponseDto from(Notice notice) {
        return new NoticeResponseDto(
                notice.getTitle(),
                notice.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(notice.getCreatedAt(), ZoneOffset.of("+9")))
        );
    }
}
