package com.claon.center.dto;

import com.claon.center.domain.Center;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CenterNameResponseDto {
    private final String id;
    private final String name;

    private CenterNameResponseDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CenterNameResponseDto from(Center center) {
        return new CenterNameResponseDto(center.getId(), center.getName());
    }
}
