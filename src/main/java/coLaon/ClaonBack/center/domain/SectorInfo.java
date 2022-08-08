package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SectorInfo {
    private String name;
    private String start;
    private String end;

    public static SectorInfo of(String name, String start, String end) {
        return new SectorInfo(
                name,
                start,
                end
        );
    }
}
