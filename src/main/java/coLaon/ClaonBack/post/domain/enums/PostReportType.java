package coLaon.ClaonBack.post.domain.enums;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum PostReportType {
    INAPPROPRIATE_POST("부적절한 게시글"),
    INAPPROPRIATE_NICKNAME("부적절한 닉네임"),
    WRONG_CENTER("잘못된 암장 선택");

    private String value;

    PostReportType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static PostReportType of(String value) {
        return Arrays.stream(PostReportType.values())
                .filter(rp -> value.equals(rp.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.INVALID_PARAMETER,
                                "잘못된 신고 유형입니다."
                        )
                );
    }
}