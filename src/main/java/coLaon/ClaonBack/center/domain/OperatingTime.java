package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperatingTime {
    private String day;
    private String start;
    private String end;

    public static OperatingTime of(String day, String start, String end) {
        return new OperatingTime(
                day,
                start,
                end
        );
    }
}
