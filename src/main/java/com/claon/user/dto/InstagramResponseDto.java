package com.claon.user.dto;

import lombok.Data;

@Data
public class InstagramResponseDto {
    private final String id;
    private final String nickname;

    private InstagramResponseDto(
            String id,
            String nickname
    ) {
        this.id = id;
        this.nickname = nickname;
    }

    public static InstagramResponseDto of(
            String id,
            String nickname
    ) {
        return new InstagramResponseDto(id, nickname);
    }
}
