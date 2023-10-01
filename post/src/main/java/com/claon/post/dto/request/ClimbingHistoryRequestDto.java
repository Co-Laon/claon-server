package com.claon.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClimbingHistoryRequestDto(
        @NotBlank(message = "홀드를 선택해주세요.")
        String holdId,
        @NotNull(message = "등반 횟수를 입력해주세요.")
        Integer climbingCount
) {
}
