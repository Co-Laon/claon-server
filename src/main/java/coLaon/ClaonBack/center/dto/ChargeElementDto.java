package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.ChargeElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeElementDto {
    private String name;
    private String fee;

    public static ChargeElementDto from(ChargeElement charge) {
        return new ChargeElementDto(
                charge.getName(),
                charge.getFee()
        );
    }
}
