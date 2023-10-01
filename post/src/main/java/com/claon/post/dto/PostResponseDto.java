package com.claon.post.dto;

import com.claon.post.common.utils.RelativeTimeUtil;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class PostResponseDto {
    private final String postId;
    private final String centerId;
    private final String userId;
    private final Integer likeCount;
    private final String content;
    private final Boolean isDeleted;
    private final String createdAt;
    private final List<String> contentsList;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private PostResponseDto(
            String postId,
            String centerId,
            String userId,
            String content,
            String createdAt,
            Boolean isDeleted,
            List<String> contentsList,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.userId = userId;
        this.likeCount = null;
        this.content = content;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.contentsList = contentsList;
        this.climbingHistories = climbingHistories;
    }

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getCenterId(),
                post.getWriterId(),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                post.getClimbingHistoryList().stream().map(
                                climbingHistory -> ClimbingHistoryResponseDto.from(
                                        climbingHistory.getHoldInfoId(),
                                        climbingHistory.getClimbingCount()))
                        .collect(Collectors.toList())
        );
    }

    public static PostResponseDto from(Post post, List<ClimbingHistory> climbingHistoryList) {
        return new PostResponseDto(
                post.getId(),
                post.getCenterId(),
                post.getWriterId(),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                climbingHistoryList.stream()
                        .map(climbingHistory -> ClimbingHistoryResponseDto.from(
                                climbingHistory.getHoldInfoId(),
                                climbingHistory.getClimbingCount()))
                        .collect(Collectors.toList())
        );
    }
}
