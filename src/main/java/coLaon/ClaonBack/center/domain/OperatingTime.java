package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OperatingTime {
    private final String day;
    private final String start;
    private final String end;

    public static OperatingTime of(String day, String start, String end) {
        return new OperatingTime(
                day,
                start,
                end
        );
    }
}
