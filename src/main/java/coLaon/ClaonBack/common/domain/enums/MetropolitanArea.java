package coLaon.ClaonBack.common.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetropolitanArea {
    SEOUL("서울시"),
    GYEONGGI("경기도");

    private final String value;

    MetropolitanArea(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
