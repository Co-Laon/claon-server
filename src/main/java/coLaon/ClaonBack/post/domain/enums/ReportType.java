package coLaon.ClaonBack.post.domain.enums;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;

import java.util.Arrays;

public enum ReportType {
    INAPPROPRIATE_POST("부적절한 게시글"),
    INAPPROPRIATE_NICKNAME("부적절한 닉네임"),
    WRONG_CENTER("잘못된 암장 선택");

    private String reportType;

    ReportType(String reportType) {
        this.reportType = reportType;
    }

    public static ReportType of(String reportType) {
        return Arrays.stream(values())
                .filter(rp -> reportType.equals(rp.reportType))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.INVALID_PARAMETER,
                                "잘못된 신고 사유입니다."
                        )
                );
    }
}