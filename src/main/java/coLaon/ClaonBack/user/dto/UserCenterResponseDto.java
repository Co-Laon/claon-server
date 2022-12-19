package coLaon.ClaonBack.user.dto;

import lombok.Data;

@Data
public class UserCenterResponseDto {

    private String centerId;
    private String centerThumbnailUrl;
    private String centerName;

    private UserCenterResponseDto(
            String centerId,
            String centerThumbnailUrl,
            String centerName
    ){
        this.centerId = centerId;
        this.centerThumbnailUrl = centerThumbnailUrl;
        this.centerName = centerName;
    }

    public static UserCenterResponseDto from (
            String centerId,
            String centerThumbnailUrl,
            String centerName
    ){
        return new UserCenterResponseDto(
                centerId, centerThumbnailUrl, centerName
        );
    }
}