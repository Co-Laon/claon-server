package com.claon.post.dto;

import com.claon.post.dto.validator.ClimbingHistorySize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {
    @NotBlank(message = "암장을 선택해주세요.")
    private String centerId;
    @Valid
    @ClimbingHistorySize(message = "1-10회 등반 기록을 입력해주세요.")
    private List<ClimbingHistoryRequestDto> climbingHistories;
    @Size(max = 500, message = "500자 이내로 내용을 입력해주세요.")
    private String content;
    @Valid
    @NotNull(message = "이미지를 업로드 해주세요.")
    @Size(min = 1, max = 10, message = "1-10개 이미지를 업로드 해주세요.")
    private List<PostContentsDto> contentsList;
}
