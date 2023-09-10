package com.claon.user.dto;

import lombok.Data;

@Data
public class ClimbingHistoryResponseDto {
    private String holdId;
    private Integer climbingCount;

    private ClimbingHistoryResponseDto(
            String holdId,
            Integer climbingCount
    ) {
        this.holdId = holdId;
        this.climbingCount = climbingCount;
    }

    public static ClimbingHistoryResponseDto from(
            String holdId,
            Integer climbingCount
    ) {
        return new ClimbingHistoryResponseDto(
                holdId,
                climbingCount
        );
    }
}
