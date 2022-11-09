package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.enums.PostReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostReportRequestDto {
    @NotNull(message = "유형을 선택해주세요.")
    private PostReportType reportType;
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 1000, message = "1000자 이내로 내용을 입력해주세요.")
    private String content;
}