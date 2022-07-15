package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Charge {
    private final String name;
    private final String fee;

    public static Charge of(String name, String fee) {
        return new Charge(
                name,
                fee
        );
    }
}
