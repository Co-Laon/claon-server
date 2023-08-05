package claon.user.dto;

import lombok.Data;

@Data
public class UserCenterPreviewResponseDto {
    private final String centerImage;
    private final String centerName;

    private UserCenterPreviewResponseDto(
            String centerImage,
            String centerName
    ) {
        this.centerImage = centerImage;
        this.centerName = centerName;
    }

    public static UserCenterPreviewResponseDto of(
            String centerImage,
            String centerName
    ) {
        return new UserCenterPreviewResponseDto(centerImage, centerName);
    }
}
