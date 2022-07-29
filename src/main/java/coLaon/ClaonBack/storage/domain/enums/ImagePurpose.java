package coLaon.ClaonBack.storage.domain.enums;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ImagePurpose {
    PROFILE("profile");

    private String value;

    ImagePurpose(String value) {
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
                                "잘못된 이미지 업로드입니다."
                        )
                );
    }
}
