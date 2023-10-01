package com.claon.center.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SectorInfoRequestDto(
        @NotBlank(message = "섹터 이름을 입력 해주세요.")
        String name,
        String start,
        String end
) {
}
