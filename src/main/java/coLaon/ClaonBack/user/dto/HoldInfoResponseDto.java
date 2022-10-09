package coLaon.ClaonBack.user.dto;

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

    public static HoldInfoResponseDto of(
            String id,
            String name,
            String image,
            String crayonImage
    ) {
        return new HoldInfoResponseDto(
                id,
                name,
                image,
                crayonImage
        );
    }
}
