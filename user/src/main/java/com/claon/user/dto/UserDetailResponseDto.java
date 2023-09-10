package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailResponseDto {
    private String nickname;
    private Long laonCount;
    private Long climbCount;
    private Float height;
    private Float armReach;
    private Float apeIndex;
    private List<CenterClimbingHistoryResponseDto> centerClimbingHistories;

    private UserDetailResponseDto(
            String nickname,
            Float height,
            Float armReach,
            Float apeIndex,
            Long laonCount,
            Long climbCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        this.nickname = nickname;
        this.laonCount = laonCount;
        this.climbCount = climbCount;
        this.height = height;
        this.armReach = armReach;
        this.apeIndex = apeIndex;
        this.centerClimbingHistories = histories;
    }

    public static UserDetailResponseDto from(
            User user,
            Long laonCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        return new UserDetailResponseDto(
                user.getNickname(),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight(),
                laonCount,
                histories.stream()
                        .map(CenterClimbingHistoryResponseDto::getClimbingHistories)
                        .mapToLong(history ->
                                history.stream()
                                        .mapToLong(ClimbingHistoryResponseDto::getClimbingCount)
                                        .sum())
                        .sum(),
                histories
        );
    }
}
