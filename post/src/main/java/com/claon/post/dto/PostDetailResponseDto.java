package com.claon.post.dto;

import com.claon.post.common.utils.RelativeTimeUtil;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostDetailResponseDto {
    private final String postId;
    private final String centerId;
    private final String userId;
//    private final String centerName;
//    private final String userProfile;
//    private final String userNickname;
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
//            String centerName,
//            String userProfile,
//            String userNickname,
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
//        this.centerName = centerName;
//        this.userProfile = userProfile;
//        this.userNickname = userNickname;
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
//                post.getCenter().getId(),
//                post.getCenter().getName(),
//                post.getWriter().getImagePath(),
//                post.getWriter().getNickname(),
                isLike,
                likeCount,
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                isOwner,
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                post.getClimbingHistoryList().stream().map(
                                climbingHistory -> ClimbingHistoryResponseDto.from(
                                        HoldInfoResponseDto.of(
                                                climbingHistory.getHoldInfoId()
//                                                climbingHistory.getHoldInfo().getName(),
//                                                climbingHistory.getHoldInfo().getImg(),
//                                                climbingHistory.getHoldInfo().getCrayonImageUrl()
                                        ),
                                        climbingHistory.getClimbingCount()))
                        .collect(Collectors.toList())
        );
    }
}
