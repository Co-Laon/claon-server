package coLaon.ClaonBack.center.domain;

import com.fasterxml.jackson.annotation.JsonValue;

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
}
