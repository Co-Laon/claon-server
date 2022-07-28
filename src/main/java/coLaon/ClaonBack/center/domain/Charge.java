package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    private String name;
    private String fee;

    public static Charge of(String name, String fee) {
        return new Charge(
                name,
                fee
        );
    }
}
