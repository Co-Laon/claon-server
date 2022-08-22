package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostReport;
import coLaon.ClaonBack.post.domain.enums.ReportType;
import lombok.Data;

@Data
public class ReportResponseDto {
    private final String postId;
    private final ReportType reportType;
    private final String content;

    private ReportResponseDto(String postId, ReportType reportType, String content) {
        this.postId = postId;
        this.reportType = reportType;
        this.content = content;
    }

    public static ReportResponseDto from(
            PostReport postReport
    ) {
        return new ReportResponseDto(
                postReport.getPost().getId(),
                postReport.getReportType(),
                postReport.getContent()
        );
    }
}