package com.claon.user.dto;

import com.claon.common.utils.RelativeTimeUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
public class UserPostDetailResponseDto {
    private final String postId;
    private final String centerId;
    private final String centerName;
    private final String userProfile;
    private final String userNickname;
    private final Boolean isLike;
    private final Integer likeCount;
    private final String content;
    private final String createdAt;
    private final List<String> contentsList;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private UserPostDetailResponseDto(
            String postId,
            String centerId,
            String centerName,
            String userProfile,
            String userNickname,
            Boolean isLike,
            Integer likeCount,
            String content,
            String createdAt,
            List<String> contentsList,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.centerName = centerName;
        this.userProfile = userProfile;
        this.userNickname = userNickname;
        this.isLike = isLike;
        this.likeCount = likeCount;
        this.content = content;
        this.createdAt = createdAt;
        this.contentsList = contentsList;
        this.climbingHistories = climbingHistories;
    }

    public static UserPostDetailResponseDto from(
            String postId,
            String centerId,
            String centerName,
            String writerImagePath,
            String writerNickname,
            Boolean isLike,
            Integer likeCount,
            String content,
            LocalDateTime createdAt,
            List<String> contentsList,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        return new UserPostDetailResponseDto(
                postId,
                centerId,
                centerName,
                writerImagePath,
                writerNickname,
                isLike,
                likeCount,
                content,
                RelativeTimeUtil.convertNow(OffsetDateTime.of(createdAt, ZoneOffset.of("+9"))),
                contentsList,
                climbingHistories
        );
    }
}
