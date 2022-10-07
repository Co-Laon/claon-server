package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.user.dto.CenterPreviewResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class CenterClimbingHistoryResponseDto {
    private CenterPreviewResponseDto center;
    private List<ClimbingHistoryResponseDto> climbingHistories;

    private CenterClimbingHistoryResponseDto(
            CenterPreviewResponseDto center,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.center = center;
        this.climbingHistories = climbingHistories;
    }

    public static CenterClimbingHistoryResponseDto from(
            CenterPreviewResponseDto center,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        return new CenterClimbingHistoryResponseDto(
                center,
                climbingHistories
        );
    }
}
