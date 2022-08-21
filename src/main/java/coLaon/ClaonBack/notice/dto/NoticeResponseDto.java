package coLaon.ClaonBack.notice.dto;

import coLaon.ClaonBack.notice.domain.Notice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeResponseDto {

    private String title;
    private String content;
    private LocalDateTime createdAt;

    private NoticeResponseDto(String title, String content, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static NoticeResponseDto from(Notice notice) {
        return new NoticeResponseDto(
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt()
        );
    }
}
