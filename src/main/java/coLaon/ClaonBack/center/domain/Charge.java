package coLaon.ClaonBack.center.domain;

import lombok.Getter;

@Getter
public class Charge {
    private final String name;
    private final String fee;

    private Charge(String name, String fee) {
        this.name = name;
        this.fee = fee;
    }

    public static Charge of(String name, String fee) {
        return new Charge(
                name,
                fee
        );
    }
}
