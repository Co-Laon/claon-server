package com.claon.post.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UserPostInfoResponseDto {
    private final String centerId;
    private final Integer postCount;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private UserPostInfoResponseDto(
            String centerId,
            Integer postCount,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.centerId = centerId;
        this.postCount = postCount;
        this.climbingHistories = climbingHistories;
    }

    public static UserPostInfoResponseDto from(
            String centerId,
            Integer postCount,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        return new UserPostInfoResponseDto(
                centerId,
                postCount,
                climbingHistories
        );
    }
}
