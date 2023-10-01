package com.claon.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequestDto(
        @NotBlank(message = "내용을 입력해주세요.")
        @Size(min = 1, max = 255, message = "255자 이내로 내용을 입력해주세요.")
        String content,
        String parentCommentId
) {
}
