package coLaon.ClaonBack.center.domain;

import lombok.Getter;

@Getter
public class SectorInfo {
    private final String name;
    private final String start;
    private final String end;

    private SectorInfo(String name, String start, String end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public static SectorInfo of(String name, String start, String end) {
        return new SectorInfo(
                name,
                start,
                end
        );
    }
}
