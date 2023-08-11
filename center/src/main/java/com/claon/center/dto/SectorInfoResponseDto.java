package com.claon.center.dto;

import com.claon.center.domain.SectorInfo;
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
