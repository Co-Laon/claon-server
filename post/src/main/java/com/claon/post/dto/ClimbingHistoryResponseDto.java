package com.claon.post.dto;

import lombok.Data;

@Data
public class ClimbingHistoryResponseDto {
    private String holdId;
//    private String holdImage;
    private Integer climbingCount;

    private ClimbingHistoryResponseDto(
            String holdId,
//            String holdImage,
            Integer climbingCount
    ) {
        this.holdId = holdId;
//        this.holdImage = holdImage;
        this.climbingCount = climbingCount;
    }

    public static ClimbingHistoryResponseDto from(
            HoldInfoResponseDto holdInfo,
            Integer climbingCount
    ) {
        return new ClimbingHistoryResponseDto(
                holdInfo.getId(),
//                holdInfo.get(),
                climbingCount
        );
    }
}
