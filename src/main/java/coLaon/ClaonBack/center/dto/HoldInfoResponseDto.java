package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.HoldInfo;
import lombok.Data;

@Data
public class HoldInfoResponseDto {
    private String name;
    private String img;

    private HoldInfoResponseDto(
            String name,
            String img
    ) {
        this.name = name;
        this.img = img;
    }

    public static HoldInfoResponseDto from(
            HoldInfo holdInfo
    ) {
        return new HoldInfoResponseDto(
                holdInfo.getName(),
                holdInfo.getImg()
        );
    }
}
