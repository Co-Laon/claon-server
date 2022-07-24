package coLaon.ClaonBack.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClimbingHistoryRequestDto {

    @NotNull
    @NotBlank
    private String holdId;
    @NotNull
    private Integer climbingCount;
}
