package claon.center.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ChargeElement {
    private String name;
    private String fee;

    public static ChargeElement of(String name, String fee) {
        return new ChargeElement(
                name,
                fee
        );
    }
}
