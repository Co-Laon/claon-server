package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.HoldInfo;
import lombok.Data;

@Data
public class HoldInfoResponseDto {
    private String id;
    private String name;
    private String image;
    private String crayonImage;

    private HoldInfoResponseDto(
            String id,
            String name,
            String image,
            String crayonImage
    ) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.crayonImage = crayonImage;
    }

    public static HoldInfoResponseDto from(
            HoldInfo holdInfo
    ) {
        String[] holdInfoImageUrl = holdInfo.getImg().split("hold/");

        String crayonImageUrl = "";
        if (holdInfoImageUrl.length == 2)
            crayonImageUrl = holdInfoImageUrl[0] + "crayon/" + holdInfoImageUrl[1];

        return new HoldInfoResponseDto(
                holdInfo.getId(),
                holdInfo.getName(),
                holdInfo.getImg(),
                crayonImageUrl
        );
    }
}
