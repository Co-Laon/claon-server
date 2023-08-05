package claon.post.dto;

import claon.post.domain.PostReport;
import claon.post.domain.enums.PostReportType;
import lombok.Data;

@Data
public class PostReportResponseDto {
    private final String postId;
    private final PostReportType reportType;
    private final String content;

    private PostReportResponseDto(String postId, PostReportType reportType, String content) {
        this.postId = postId;
        this.reportType = reportType;
        this.content = content;
    }

    public static PostReportResponseDto from(
            PostReport postReport
    ) {
        return new PostReportResponseDto(
                postReport.getPost().getId(),
                postReport.getPostReportType(),
                postReport.getContent()
        );
    }
}