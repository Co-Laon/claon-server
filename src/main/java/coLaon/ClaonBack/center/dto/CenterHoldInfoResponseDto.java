package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.HoldInfo;
import lombok.Data;

@Data
public class CenterHoldInfoResponseDto {
    private String id;
    private String name;
    private String image;
    private String crayonImage;

    private CenterHoldInfoResponseDto(
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

    public static CenterHoldInfoResponseDto from(
            HoldInfo holdInfo
    ) {
        String[] holdInfoImageUrl = holdInfo.getImg().split("hold/");

        String crayonImageUrl = "";
        if (holdInfoImageUrl.length == 2)
            crayonImageUrl = holdInfoImageUrl[0] + "crayon/" + holdInfoImageUrl[1];

        return new CenterHoldInfoResponseDto(
                holdInfo.getId(),
                holdInfo.getName(),
                holdInfo.getImg(),
                crayonImageUrl
        );
    }
}
