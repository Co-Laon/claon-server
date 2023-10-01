package com.claon.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignInRequestDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        String email
) {
}
