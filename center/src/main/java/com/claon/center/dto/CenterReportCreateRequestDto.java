package com.claon.center.dto;

import com.claon.center.domain.enums.CenterReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterReportCreateRequestDto {
    @NotBlank(message = "요청 내용을 입력해주세요.")
    private String content;
    @NotNull(message = "요청 부분을 선택해주세요.")
    private CenterReportType reportType;
}
