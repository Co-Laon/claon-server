package com.claon.center.dto;

import com.claon.center.domain.CenterReport;
import com.claon.center.domain.enums.CenterReportType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CenterReportResponseDto {
    private final String id;
    private final String content;
    private final CenterReportType reportType;
    private final String reporterId;
    private final String centerId;
    private final String centerName;

    private CenterReportResponseDto(
            String id,
            String content,
            CenterReportType reportType,
            String reporterId,
            String centerId,
            String centerName
    ) {
        this.id = id;
        this.content = content;
        this.reportType = reportType;
        this.reporterId = reporterId;
        this.centerId = centerId;
        this.centerName = centerName;
    }

    public static CenterReportResponseDto from(
            CenterReport centerReport
    ) {
        return new CenterReportResponseDto(
                centerReport.getId(),
                centerReport.getContent(),
                centerReport.getReportType(),
                centerReport.getReporterId(),
                centerReport.getCenter().getId(),
                centerReport.getCenter().getName()
        );
    }
}
