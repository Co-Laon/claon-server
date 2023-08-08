package com.claon.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequestDto {
    @NotNull(message = "평점을 입력해주세요.")
    @Max(value = 5, message = "5점 이하로 평점을 입력해주세요.")
    @Min(value = 0, message = "0점 이상으로 평점을 입력해주세요.")
    private Integer rank;
    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    @Size(min = 1, max = 500, message = "500자 이내로 내용을 입력해주세요.")
    private String content;
}
