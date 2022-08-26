package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.enums.PostReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostReportRequestDto {
    @NotNull(message = "신고 유형을 입력해주세요.")
    private PostReportType reportType;
    @NotBlank(message = "신고 내용을 입력해주세요.")
    private String content;
}