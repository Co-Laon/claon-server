package coLaon.ClaonBack.common.domain.enums;


import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

import java.util.Arrays;

public enum MetropolitanArea {
    SEOUL("서울시"),
    GYEONGGI("경기도");

    private String value;

    MetropolitanArea(String value) {
        this.value = value;
    }

    public static String of(String value) {
        return Arrays.stream(values())
                .filter(v -> value.equals(v.value))
                .findFirst()
                .map(metropolitanArea -> metropolitanArea.value)
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_ADDRESS,
                                "잘못된 주소입니다."
                        )
                );
    }
}
