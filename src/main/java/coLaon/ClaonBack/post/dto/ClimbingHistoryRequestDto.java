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
    @NotBlank(message = "홀드가 선택해주세요.")
    private String holdId;
    @NotNull(message = "등반 횟수를 입력해주세요.")
    private Integer climbingCount;
}
