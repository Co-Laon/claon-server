package com.claon.center.domain.enums;

import com.claon.common.exception.BadRequestException;
import com.claon.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum CenterReportType {
    TELEPHONE("연락처"),
    PICTURE("사진"),
    OPERATING_TIME("운영시간"),
    FACILITIES("편의시설"),
    CHARGE("이용요금"),
    HOLD("홀드정보"),
    SETTING_INFO("세팅일정");

    private String value;

    CenterReportType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static CenterReportType of(String value) {
        return Arrays.stream(CenterReportType.values())
                .filter(v -> value.equalsIgnoreCase(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_CENTER_REPORT_TYPE,
                                String.format("%s은 지원하지 않는 요청 부분입니다.", value)
                        )
                );
    }
}
