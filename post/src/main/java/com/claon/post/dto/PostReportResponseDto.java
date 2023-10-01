package com.claon.post.dto;

import com.claon.post.domain.PostReport;
import com.claon.post.domain.enums.PostReportType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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