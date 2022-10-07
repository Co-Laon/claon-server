package coLaon.ClaonBack.user.dto;

import lombok.Data;

@Data
public class CenterPreviewResponseDto {
    private final String centerImage;
    private final String centerName;

    private CenterPreviewResponseDto(
            String centerImage,
            String centerName
    ) {
        this.centerImage = centerImage;
        this.centerName = centerName;
    }

    public static CenterPreviewResponseDto of(
            String centerImage,
            String centerName
    ) {
        return new CenterPreviewResponseDto(centerImage, centerName);
    }
}
