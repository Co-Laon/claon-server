package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.Charge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDto {
    private String name;
    private String fee;

    public static ChargeDto from(Charge charge) {
        return new ChargeDto(
                charge.getName(),
                charge.getFee()
        );
    }
}
