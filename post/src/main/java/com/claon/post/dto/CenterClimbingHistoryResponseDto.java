package com.claon.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class CenterClimbingHistoryResponseDto {
    private String centerId;
    private Integer postCount;
    private List<ClimbingHistoryResponseDto> climbingHistories;

    private CenterClimbingHistoryResponseDto(
            String centerId,
            Integer postCount,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.centerId = centerId;
        this.postCount = postCount;
        this.climbingHistories = climbingHistories;
    }

    public static CenterClimbingHistoryResponseDto from(
            String centerId,
            Integer postCount,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        return new CenterClimbingHistoryResponseDto(
                centerId,
                postCount,
                climbingHistories
        );
    }
}
