package com.claon.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 1, max = 255, message = "255자 이내로 내용을 입력해주세요.")
    private String content;
    private String parentCommentId;
}
