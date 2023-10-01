package com.claon.auth.dto.request;

import com.claon.auth.common.validator.UserNickname;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequestDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        @UserNickname
        String nickname,
        Float height,
        Float armReach
) {
}
