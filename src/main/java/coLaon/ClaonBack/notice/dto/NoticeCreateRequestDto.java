package coLaon.ClaonBack.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeCreateRequestDto {

    private String title;
    private String content;
}
