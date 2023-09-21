package com.claon.post.dto;

import lombok.Data;

@Data
public class CenterPostThumbnailResponseDto {
    private final String postId;
    private final String thumbnailUrl;

    private CenterPostThumbnailResponseDto(
            String postId,
            String thumbnailUrl
    ) {
        this.postId = postId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static CenterPostThumbnailResponseDto from(
            String postId,
            String thumbnailUrl
    ) {
        return new CenterPostThumbnailResponseDto(
                postId,
                thumbnailUrl
        );
    }
}
