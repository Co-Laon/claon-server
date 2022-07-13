package coLaon.ClaonBack.center.domain;

import lombok.Getter;

@Getter
public class OperatingTime {
    private final String day;
    private final String start;
    private final String end;

    private OperatingTime(String day, String start, String end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public static OperatingTime of(String day, String start, String end) {
        return new OperatingTime(
                day,
                start,
                end
        );
    }
}
