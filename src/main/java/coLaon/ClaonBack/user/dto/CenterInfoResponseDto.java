package coLaon.ClaonBack.user.dto;

import lombok.Data;

@Data
public class CenterInfoResponseDto {
    private String centerId;
    private String centerName;
    private String centerImg;

    private CenterInfoResponseDto(String centerId, String centerName, String centerImg) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.centerImg = centerImg;
    }

    public static CenterInfoResponseDto from(
            String centerId,
            String centerName,
            String centerImg
    ) {
        return new CenterInfoResponseDto(centerId, centerName, centerImg);
    }
}
