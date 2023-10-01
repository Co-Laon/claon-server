package com.claon.user.service.client.dto;

import java.util.List;

public record PostThumbnailResponse(
        String postId,
        String thumbnailUrl,
        List<ClimbingHistoryResponse> climbingHistories
) {
}
