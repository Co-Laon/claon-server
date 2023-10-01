package com.claon.center.dto.request;

import com.claon.center.domain.enums.CenterReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CenterReportRequestDto(
        @NotBlank(message = "요청 내용을 입력해주세요.")
        String content,
        @NotNull(message = "요청 부분을 선택해주세요.")
        CenterReportType reportType
) {
}
