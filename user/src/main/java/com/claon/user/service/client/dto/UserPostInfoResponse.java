package com.claon.user.service.client.dto;

import java.util.List;

public record UserPostInfoResponse(
        String centerId,
        Integer postCount,
        List<ClimbingHistoryResponse>climbingHistories
) {
}
