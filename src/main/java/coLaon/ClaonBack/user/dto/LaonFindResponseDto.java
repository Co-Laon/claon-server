package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.Laon;
import lombok.Data;

@Data
public class LaonFindResponseDto {
    private String laonNickname;
    private String laonProfileImage;

    private LaonFindResponseDto(String laonNickname, String laonProfileImage) {
        this.laonNickname = laonNickname;
        this.laonProfileImage = laonProfileImage;
    }

    public static LaonFindResponseDto from(Laon laon) {
        return new LaonFindResponseDto(
                laon.getLaon().getNickname(),
                laon.getLaon().getImagePath()
        );
    }
}
