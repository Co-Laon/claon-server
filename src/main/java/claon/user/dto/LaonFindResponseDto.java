package claon.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class LaonFindResponseDto {
    private String laonNickname;
    private String laonProfileImage;

    @QueryProjection
    public LaonFindResponseDto(String laonNickname, String laonProfileImage) {
        this.laonNickname = laonNickname;
        this.laonProfileImage = laonProfileImage;
    }
}
