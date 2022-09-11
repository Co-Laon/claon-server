package coLaon.ClaonBack.center.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CenterSearchOption {
    NEW_SETTING("new_setting"),
    BOOKMARK("bookmark"),
    MY_AROUND("my_around"),
    NEWLY_REGISTERED("newly_registered");

    private String value;
    
    CenterSearchOption(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
