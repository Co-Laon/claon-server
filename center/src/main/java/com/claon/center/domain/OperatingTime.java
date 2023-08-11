package com.claon.center.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OperatingTime {
    private String day;
    private String start;
    private String end;

    public static OperatingTime of(String day, String start, String end) {
        return new OperatingTime(
                day,
                start,
                end
        );
    }
}
