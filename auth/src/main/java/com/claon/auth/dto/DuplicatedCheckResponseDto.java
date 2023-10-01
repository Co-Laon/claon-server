package com.claon.auth.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DuplicatedCheckResponseDto {
    private final boolean result;

    private DuplicatedCheckResponseDto(boolean result) {
        this.result = result;
    }

    public static DuplicatedCheckResponseDto of(boolean result) {
        return new DuplicatedCheckResponseDto(result);
    }
}
