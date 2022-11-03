package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.common.utils.RelativeTimeUtil;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.user.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HoldInfoResponseDto;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostDetailResponseDto {
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

    private PostDetailResponseDto(
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

    public static PostDetailResponseDto from(Post post, Boolean isLike, Integer likeCount) {
        return new PostDetailResponseDto(
                post.getId(),
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                isLike,
                likeCount,
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                post.getClimbingHistorySet().stream().map(
                                climbingHistory -> ClimbingHistoryResponseDto.from(
                                        HoldInfoResponseDto.of(
                                                climbingHistory.getHoldInfo().getId(),
                                                climbingHistory.getHoldInfo().getName(),
                                                climbingHistory.getHoldInfo().getImg(),
                                                climbingHistory.getHoldInfo().getCrayonImageUrl()
                                        ),
                                        climbingHistory.getClimbingCount()))
                        .collect(Collectors.toList())
        );
    }
}
