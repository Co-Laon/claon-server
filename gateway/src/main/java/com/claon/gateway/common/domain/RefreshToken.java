package com.claon.gateway.common.domain;

import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RefreshToken {
    @Id
    private final String key;
    private final String userId;

    private RefreshToken(String key, String userId) {
        this.key = key;
        this.userId = userId;
    }

    public static RefreshToken of(String userId) {
        String key = UUID.randomUUID().toString();
        return new RefreshToken(key, userId);
    }

    public static RefreshToken of(String key, String userId) {
        return new RefreshToken(key, userId);
    }
}
