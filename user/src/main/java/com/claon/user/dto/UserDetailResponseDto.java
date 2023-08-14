package com.claon.user.dto;

import com.claon.user.domain.User;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class UserDetailResponseDto {
    private String nickname;
    private Long postCount;
    private Long laonCount;
    private Long climbCount;
    private Float height;
    private Float armReach;
    private Float apeIndex;
    private String imagePath;
    private String instagramUrl;
    private Boolean isPrivate;
    private List<CenterClimbingHistoryResponseDto> centerClimbingHistories;

    private UserDetailResponseDto(
            String nickname,
            Boolean isPrivate,
            String imagePath,
            String instagramUrl,
            Float height,
            Float armReach,
            Float apeIndex,
            Long postCount,
            Long laonCount,
            Long climbCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        this.nickname = nickname;
        this.postCount = postCount;
        this.laonCount = laonCount;
        this.isPrivate = isPrivate;
        this.imagePath = imagePath;
        this.climbCount = climbCount;
        this.height = height;
        this.armReach = armReach;
        this.apeIndex = apeIndex;
        this.instagramUrl = instagramUrl;
        this.centerClimbingHistories = histories;
    }

    public static UserDetailResponseDto from(
            User user,
            Long postCount,
            Long laonCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        return new UserDetailResponseDto(
                user.getNickname(),
                user.getIsPrivate(),
                user.getImagePath(),
                Optional.ofNullable(user.getInstagramUserName()).map(name -> "https://instagram.com/" + name).orElse(null),
                user.getHeight(),
                user.getArmReach(),
                user.getArmReach() - user.getHeight(),
                postCount,
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
