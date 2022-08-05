package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.HoldInfo;
import lombok.Data;

@Data
public class HoldInfoResponseDto {
    private String id;
    private String name;
    private String image;

    private HoldInfoResponseDto(
            String id,
            String name,
            String image
    ) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public static HoldInfoResponseDto from(
            HoldInfo holdInfo
    ) {
        return new HoldInfoResponseDto(
                holdInfo.getId(),
                holdInfo.getName(),
                holdInfo.getImg()
        );
    }
}
