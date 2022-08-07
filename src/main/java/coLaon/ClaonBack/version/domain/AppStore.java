package coLaon.ClaonBack.version.domain;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

import java.util.Arrays;

public enum AppStore {
    GOOGLE("aos"),
    APPLE("ios");

    private String value;

    AppStore(String value) {
        this.value = value;
    }

    public static String of(String value) {
        return Arrays.stream(values())
                .filter(v -> value.equals(v.value))
                .findFirst()
                .map(appStore -> appStore.value)
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_STORE,
                                "지원하지 않는 스토어 입니다."
                        )
                );
    }
}
