package com.claon.center.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequestDto {
    @NotNull(message = "점수를 매겨주세요")
    @Max(value = 5)
    @Min(value = 1)
    private Integer rank;
    @NotBlank(message = "리뷰 내용을 작성해주세요")
    @Size(min = 1, max = 500, message = "리뷰 최대 글자수는 500자입니다")
    private String content;
}
