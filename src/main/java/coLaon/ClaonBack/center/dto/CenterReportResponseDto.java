package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.CenterReport;
import coLaon.ClaonBack.center.domain.enums.CenterReportType;
import lombok.Data;

@Data
public class CenterReportResponseDto {
    private String id;
    private String content;
    private CenterReportType reportType;
    private String reporterNickname;
    private String reporterProfileImage;
    private String centerId;
    private String centerName;

    private CenterReportResponseDto(
            String id,
            String content,
            CenterReportType reportType,
            String reporterNickname,
            String reporterProfileImage,
            String centerId,
            String centerName
    ) {
        this.id = id;
        this.content = content;
        this.reportType = reportType;
        this.reporterNickname = reporterNickname;
        this.reporterProfileImage = reporterProfileImage;
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
                centerReport.getReporter().getNickname(),
                centerReport.getReporter().getImagePath(),
                centerReport.getCenter().getId(),
                centerReport.getCenter().getName()
        );
    }
}
