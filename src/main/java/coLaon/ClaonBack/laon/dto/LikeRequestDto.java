package coLaon.ClaonBack.laon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {
    @NotNull
    private String laonId;
}
