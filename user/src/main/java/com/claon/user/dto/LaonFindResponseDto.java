package com.claon.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class LaonFindResponseDto {
    private String laonNickname;

    @QueryProjection
    public LaonFindResponseDto(String laonNickname) {
        this.laonNickname = laonNickname;
    }
}
