package com.claon.gateway.common.domain;

import lombok.Getter;

import java.util.Optional;

@Getter
public class JwtDto {
    private final String accessToken;
    private final String refreshToken;
    private Boolean isCompletedSignUp;

    private JwtDto(
            String accessToken,
            String refreshToken,
            Boolean isCompletedSignUp
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isCompletedSignUp = isCompletedSignUp;
    }

    private JwtDto(
            String accessToken,
            String refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static JwtDto of(
            String accessToken,
            String refreshToken,
            Boolean isCompletedSignUp
    ) {
        return new JwtDto(
                accessToken,
                refreshToken,
                isCompletedSignUp
        );
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

    public Optional<Boolean> getIsCompletedSignUp() {
        return Optional.ofNullable(isCompletedSignUp);
    }
}
