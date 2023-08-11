package com.claon.center.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectorInfoRequestDto {
    @NotBlank(message = "섹터 이름을 입력 해주세요.")
    private String name;
    private String start;
    private String end;
}
