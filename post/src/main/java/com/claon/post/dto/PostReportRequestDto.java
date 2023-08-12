package com.claon.post.dto;

import com.claon.post.domain.enums.PostReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostReportRequestDto {
    @NotNull(message = "유형을 선택해주세요.")
    private PostReportType reportType;
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 1000, message = "1000자 이내로 내용을 입력해주세요.")
    private String content;
}