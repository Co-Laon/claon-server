package coLaon.ClaonBack.common.domain.enums;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

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

    @JsonCreator
    public static MetropolitanArea of(String value) {
        return Arrays.stream(MetropolitanArea.values())
                .filter(v -> value.equals(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_ADDRESS,
                                "잘못된 주소입니다."
                        )
                );
    }
}
