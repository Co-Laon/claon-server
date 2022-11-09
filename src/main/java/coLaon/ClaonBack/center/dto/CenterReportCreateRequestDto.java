package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.enums.CenterReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterReportCreateRequestDto {
    @NotBlank(message = "요청 내용을 입력해주세요.")
    private String content;
    @NotNull(message = "요청 부분을 선택해주세요.")
    private CenterReportType reportType;
}
