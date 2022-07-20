package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.SectorInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectorInfoDto {
    private String name;
    private String start;
    private String end;

    public static SectorInfoDto from(SectorInfo sectorInfo) {
        return new SectorInfoDto(
                sectorInfo.getName(),
                sectorInfo.getStart(),
                sectorInfo.getEnd()
        );
    }
}
