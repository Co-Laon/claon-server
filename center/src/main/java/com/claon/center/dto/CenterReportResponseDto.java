package com.claon.center.dto;

import com.claon.center.domain.CenterReport;
import com.claon.center.domain.enums.CenterReportType;
import lombok.Data;

@Data
public class CenterReportResponseDto {
    private String id;
    private String content;
    private CenterReportType reportType;
    private String reporterId;
//    private String reporterNickname;
//    private String reporterProfileImage;
    private String centerId;
    private String centerName;

    private CenterReportResponseDto(
            String id,
            String content,
            CenterReportType reportType,
            String reporterId,
//            String reporterNickname,
//            String reporterProfileImage,
            String centerId,
            String centerName
    ) {
        this.id = id;
        this.content = content;
        this.reportType = reportType;
        this.reporterId = reporterId;
//        this.reporterNickname = reporterNickname;
//        this.reporterProfileImage = reporterProfileImage;
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
//                centerReport.getReporter().getNickname(),
//                centerReport.getReporter().getImagePath(),
                centerReport.getCenter().getId(),
                centerReport.getCenter().getName()
        );
    }
}