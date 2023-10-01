package com.claon.post.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ClimbingHistoryResponseDto {
    private final String holdId;
    private final Integer climbingCount;

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
