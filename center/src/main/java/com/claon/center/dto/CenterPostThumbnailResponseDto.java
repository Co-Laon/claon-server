package com.claon.center.dto;

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

    public static CenterPostThumbnailResponseDto from(String id, String thumbnailUrl) {
        return new CenterPostThumbnailResponseDto(
                id,
                thumbnailUrl
        );
    }
}
