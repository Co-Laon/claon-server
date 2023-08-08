package com.claon.version.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AppStore {
    GOOGLE("aos"),
    APPLE("ios");

    private String value;

    AppStore(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
