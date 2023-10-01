package com.claon.center.dto;

import com.claon.center.domain.HoldInfo;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HoldInfoResponseDto {
    private final String id;
    private final String name;
    private final String image;
    private final String crayonImage;

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
