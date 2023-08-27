package com.claon.gateway.common.domain;

import lombok.Getter;

import java.util.Optional;

@Getter
public class JwtDto {
    private final String accessToken;
    private final String refreshToken;

    private JwtDto(
            String accessToken,
            String refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static JwtDto of(
            String accessToken,
            String refreshToken
    ) {
        return new JwtDto(
                accessToken,
                refreshToken
        );
    }
}
