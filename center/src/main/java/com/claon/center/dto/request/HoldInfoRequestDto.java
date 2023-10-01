package com.claon.center.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HoldInfoRequestDto(
        @NotBlank(message = "홀드 이름을 입력 해주세요.")
        String name,
        @NotBlank(message = "홀드 이미지를 입력 해주세요.")
        String img
) {
}
