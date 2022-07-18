package coLaon.ClaonBack.storage.domain.enums;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

import java.util.Arrays;

public enum Purpose {
    PROFILE("profile");

    private String value;

    Purpose(String value) {
        this.value = value;
    }

    public static String of(String value) {
        return Arrays.stream(values())
                .filter(v -> value.equals(v.value))
                .findFirst()
                .map(purpose -> purpose.value)
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_PURPOSE,
                                "잘못된 용도입니다."
                        )
                );
    }
}
