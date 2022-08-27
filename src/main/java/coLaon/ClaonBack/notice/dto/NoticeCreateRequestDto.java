package coLaon.ClaonBack.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeCreateRequestDto {

    private String title;
    private String content;
}
