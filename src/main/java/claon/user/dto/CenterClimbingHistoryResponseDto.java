package claon.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class CenterClimbingHistoryResponseDto {
    private UserCenterPreviewResponseDto center;
    private List<ClimbingHistoryResponseDto> climbingHistories;

    private CenterClimbingHistoryResponseDto(
            UserCenterPreviewResponseDto center,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.center = center;
        this.climbingHistories = climbingHistories;
    }

    public static CenterClimbingHistoryResponseDto from(
            UserCenterPreviewResponseDto center,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        return new CenterClimbingHistoryResponseDto(
                center,
                climbingHistories
        );
    }
}
