package com.claon.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NoticeRequestDto(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}
