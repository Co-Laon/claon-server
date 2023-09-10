package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class IndividualUserResponseDto {
    private String nickname;
    private Long laonCount;
    private Long climbCount;
    private Float height;
    private Float armReach;
    private Float apeIndex;
    private Boolean isLaon;
    private List<CenterClimbingHistoryResponseDto> centerClimbingHistories;

    private IndividualUserResponseDto(
            User user,
            Boolean isLaon,
            Long laonCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        this.nickname = user.getNickname();
        this.isLaon = isLaon;
        this.laonCount = laonCount;
        this.climbCount = histories.stream()
                .map(CenterClimbingHistoryResponseDto::getClimbingHistories)
                .mapToLong(history ->
                        history.stream()
                                .mapToLong(ClimbingHistoryResponseDto::getClimbingCount)
                                .sum())
                .sum();
        this.height = user.getHeight();
        this.armReach = user.getArmReach();
        this.apeIndex = user.getArmReach() - user.getHeight();
        this.centerClimbingHistories = histories;
    }

    public static IndividualUserResponseDto from(
            User user,
            Boolean isLaon,
            Long laonCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        return new IndividualUserResponseDto(
                user,
                isLaon,
                laonCount,
                histories
        );
    }
}
