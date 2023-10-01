package com.claon.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class LaonResponseDto {
    private String laonNickname;

    @QueryProjection
    public LaonResponseDto(String laonNickname) {
        this.laonNickname = laonNickname;
    }
}
