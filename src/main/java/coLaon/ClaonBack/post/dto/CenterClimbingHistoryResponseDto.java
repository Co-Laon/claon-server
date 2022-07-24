package coLaon.ClaonBack.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CenterClimbingHistoryResponseDto {
    private String centerName;
    private List<ClimbingHistoryResponseDto> climbingHistories;

    public CenterClimbingHistoryResponseDto(String centerName, List<ClimbingHistoryResponseDto> climbingHistories) {

        this.centerName = centerName;
        this.climbingHistories = climbingHistories;
    }

}
