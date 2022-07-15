package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectorInfo {
    private final String name;
    private final String start;
    private final String end;

    public static SectorInfo of(String name, String start, String end) {
        return new SectorInfo(
                name,
                start,
                end
        );
    }
}
