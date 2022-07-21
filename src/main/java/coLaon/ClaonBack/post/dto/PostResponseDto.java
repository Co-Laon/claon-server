package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.common.utils.RelativeTimeUtil;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class PostResponseDto {
    private final String postId;
    private final String centerId;
    private final String centerNickname;
    private final String userProfile;
    private final String userNickname;
    private final Long likeCount;
    private final List<String> holdIdList;
    private final String content;
    private final Boolean isDeleted;
    private final String createdAt;
    private final List<String> contentsList;

    private PostResponseDto(
            String postId,
            String centerId,
            String centerNickname,
            String userProfile,
            String userNickname,
            List<String> holdIdList,
            String content,
            String createdAt,
            Boolean isDeleted,
            List<String> contentsList
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.centerNickname = centerNickname;
        this.userProfile = userProfile;
        this.userNickname = userNickname;
        this.likeCount = null;
        this.holdIdList = holdIdList;
        this.content = content;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.contentsList = contentsList;
    }

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                post.getClimbingHistorySet().stream().map(ClimbingHistory::getHoldInfo).map(HoldInfo::getId).collect(Collectors.toList()),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentsSet().stream().map(PostContents::getUrl).collect(Collectors.toList())
        );
    }

    public static PostResponseDto from(Post post, List<String> postContentsList, List<String> holdIdList) {
        return new PostResponseDto(
                post.getId(),
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                holdIdList,
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                postContentsList
        );
    }
}
