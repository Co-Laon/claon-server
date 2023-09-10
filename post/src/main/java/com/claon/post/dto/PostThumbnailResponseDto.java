package com.claon.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostThumbnailResponseDto {
    private final String postId;
    private final String thumbnailUrl;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private PostThumbnailResponseDto(
            String postId,
            String thumbnailUrl,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.thumbnailUrl = thumbnailUrl;
        this.climbingHistories = climbingHistories;
    }

    public static PostThumbnailResponseDto from(
            String postId,
            String thumbnailUrl,
            List<ClimbingHistoryResponseDto> climbingHistories

    ) {
        return new PostThumbnailResponseDto(
                postId,
                thumbnailUrl,
                climbingHistories
        );
    }
}
