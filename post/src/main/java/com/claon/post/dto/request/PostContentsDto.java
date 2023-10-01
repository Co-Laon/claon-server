package com.claon.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PostContentsDto(
        @NotBlank(message = "이미지를 업로드 해주세요.")
        @Pattern(regexp = "(\\S+(\\.(?i)(jpe?g|png))$)", message = "이미지만 업로드 해주세요.")
        String url
) {
}
