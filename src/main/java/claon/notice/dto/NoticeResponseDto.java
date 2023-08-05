package claon.notice.dto;

import claon.common.utils.RelativeTimeUtil;
import claon.notice.domain.Notice;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
public class NoticeResponseDto {
    private String title;
    private String content;
    private String createdAt;

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
