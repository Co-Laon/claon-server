package com.claon.center.dto;

import com.claon.center.domain.SectorInfo;
import lombok.Getter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class SectorInfoResponseDto {
    private final String id;
    private final String name;
    private final String start;
    private final String end;

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
        String startDate = "";
        String endDate = "";
        if (sectorInfo.getStart() != null) {
            startDate = sectorInfo.getStart().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }

        if (sectorInfo.getEnd() != null) {
            endDate = sectorInfo.getEnd().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }

        return new SectorInfoResponseDto(
                sectorInfo.getId(),
                sectorInfo.getName(),
                startDate,
                endDate
        );
    }
}
