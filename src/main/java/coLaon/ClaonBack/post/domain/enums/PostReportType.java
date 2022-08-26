package coLaon.ClaonBack.post.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

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
}