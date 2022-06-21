package coLaon.ClaonBack.laon.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LaonCreateRequestDto {
    private String centerName;
    private String wallName;
    private String holdInfo;
    private String videoUrl;
    private String videoThumbnailUrl;
    private String content;
    private User user;
}
