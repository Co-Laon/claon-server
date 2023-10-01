package com.claon.user.dto;

import com.claon.user.domain.User;
import com.claon.user.service.client.dto.ClimbingHistoryResponse;
import com.claon.user.service.client.dto.UserPostInfoResponse;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UserDetailResponseDto {
    private final String nickname;
    private final Long laonCount;
    private final Long climbingCount;
    private final Float height;
    private final Float armReach;
    private final Float apeIndex;
    private final Boolean isLaon;
    private final List<UserPostInfoResponse> postInfoList;

    private UserDetailResponseDto(
            String nickname,
            Float height,
            Float armReach,
            Float apeIndex,
            Long laonCount,
            Long climbingCount,
            Boolean isLaon,
            List<UserPostInfoResponse> postInfoList
    ) {
        this.nickname = nickname;
        this.laonCount = laonCount;
        this.climbingCount = climbingCount;
        this.height = height;
        this.armReach = armReach;
        this.apeIndex = apeIndex;
        this.isLaon = isLaon;
        this.postInfoList = postInfoList;
    }

    public static UserDetailResponseDto from(
            User user,
            Long laonCount,
            List<UserPostInfoResponse> postInfoList
    ) {
        return new UserDetailResponseDto(
                user.getNickname(),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight(),
                laonCount,
                postInfoList.stream()
                        .map(UserPostInfoResponse::climbingHistories)
                        .mapToLong(history ->
                                history.stream()
                                        .mapToLong(ClimbingHistoryResponse::climbingCount)
                                        .sum())
                        .sum(),
                true,
                postInfoList
        );
    }

    public static UserDetailResponseDto from(
            User user,
            Long laonCount,
            Boolean isLaon,
            List<UserPostInfoResponse> postInfoList
    ) {
        return new UserDetailResponseDto(
                user.getNickname(),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight(),
                laonCount,
                postInfoList.stream()
                        .map(UserPostInfoResponse::climbingHistories)
                        .mapToLong(history ->
                                history.stream()
                                        .mapToLong(ClimbingHistoryResponse::climbingCount)
                                        .sum())
                        .sum(),
                isLaon,
                postInfoList
        );
    }
}
