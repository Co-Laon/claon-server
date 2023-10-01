package com.claon.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequestDto(
        @NotBlank(message = "댓글을 입력하세요")
        @Size(min = 1, max = 255, message = "댓글 최대 글자수는 255자입니다")
        String content
) {
}
