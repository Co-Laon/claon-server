package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.SectorInfo;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class SectorInfoResponseDto {
    private String id;
    private String name;
    private String start;
    private String end;

    private SectorInfoResponseDto(
            String id,
            String name,
            String start,
            String end
    ) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public static SectorInfoResponseDto from(
            SectorInfo sectorInfo
    ) {
        return new SectorInfoResponseDto(
                sectorInfo.getId(),
                sectorInfo.getName(),
                sectorInfo.getStart().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                sectorInfo.getEnd().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        );
    }
}
