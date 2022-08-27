package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
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

    public static CenterReportType of(String value) {
        return Arrays.stream(values())
                .filter(v -> value.equalsIgnoreCase(v.value))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_CENTER_REPORT_TYPE,
                                String.format("'%s' is invalid : not supported", value)
                        )
                );
    }
}
