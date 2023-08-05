package claon.center.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    private List<ChargeElement> chargeList;
    private String image;

    public static Charge of(List<ChargeElement> chargeList, String image) {
        return new Charge(
                chargeList,
                image
        );
    }
}
