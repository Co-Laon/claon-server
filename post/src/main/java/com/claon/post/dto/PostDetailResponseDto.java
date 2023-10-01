package com.claon.post.dto;

import com.claon.post.common.utils.RelativeTimeUtil;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class PostDetailResponseDto {
    private final String postId;
    private final String centerId;
    private final String userId;
    private final Boolean isLike;
    private final Integer likeCount;
    private final String content;
    private final String createdAt;
    private final Boolean isOwner;
    private final List<String> contentsList;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private PostDetailResponseDto(
            String postId,
            String centerId,
            String userId,
            Boolean isLike,
            Integer likeCount,
            String content,
            String createdAt,
            Boolean isOwner,
            List<String> contentsList,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.userId = userId;
        this.isLike = isLike;
        this.likeCount = likeCount;
        this.content = content;
        this.createdAt = createdAt;
        this.isOwner = isOwner;
        this.contentsList = contentsList;
        this.climbingHistories = climbingHistories;
    }

    public static PostDetailResponseDto from(Post post, Boolean isOwner, Boolean isLike, Integer likeCount) {
        return new PostDetailResponseDto(
                post.getId(),
                post.getCenterId(),
                post.getWriterId(),
                isLike,
                likeCount,
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                isOwner,
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                post.getClimbingHistoryList().stream().map(
                                climbingHistory -> ClimbingHistoryResponseDto.from(
                                        climbingHistory.getHoldInfoId(),
                                        climbingHistory.getClimbingCount()))
                        .collect(Collectors.toList())
        );
    }
}
