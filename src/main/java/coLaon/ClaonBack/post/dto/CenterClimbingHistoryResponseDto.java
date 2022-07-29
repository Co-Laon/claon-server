package coLaon.ClaonBack.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class CenterClimbingHistoryResponseDto {
    private String centerName;
    private List<ClimbingHistoryResponseDto> climbingHistories;

    private CenterClimbingHistoryResponseDto(
            String centerName,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.centerName = centerName;
        this.climbingHistories = climbingHistories;
    }

    public static CenterClimbingHistoryResponseDto from(
            String name,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        return new CenterClimbingHistoryResponseDto(
                name,
                climbingHistories
        );
    }
}
