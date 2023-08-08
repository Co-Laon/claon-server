package com.claon.user.domain.enums;

import lombok.Getter;

@Getter
public enum OAuth2Provider {
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String value;

    OAuth2Provider(String value) {
        this.value = value;
    }
}
