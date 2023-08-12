package com.claon.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClimbingHistoryRequestDto {
    @NotBlank(message = "홀드를 선택해주세요.")
    private String holdId;
    @NotNull(message = "등반 횟수를 입력해주세요.")
    private Integer climbingCount;
}
