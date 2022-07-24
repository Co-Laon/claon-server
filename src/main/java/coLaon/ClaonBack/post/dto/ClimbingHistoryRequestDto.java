package coLaon.ClaonBack.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClimbingHistoryRequestDto {

    private String holdId;
    private Integer climbingCount;
}
