package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.common.utils.RelativeTimeUtil;
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
    private final String centerName;
    private final String userProfile;
    private final String userNickname;
    private final Integer likeCount;
    private final List<HoldInfoResponseDto> holdList;
    private final String content;
    private final Boolean isDeleted;
    private final String createdAt;
    private final List<String> contentsList;

    private PostResponseDto(
            String postId,
            String centerId,
            String centerName,
            String userProfile,
            String userNickname,
            List<HoldInfoResponseDto> holdList,
            String content,
            String createdAt,
            Boolean isDeleted,
            List<String> contentsList
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.centerName = centerName;
        this.userProfile = userProfile;
        this.userNickname = userNickname;
        this.likeCount = null;
        this.holdList = holdList;
        this.content = content;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.contentsList = contentsList;
    }

    private PostResponseDto(
            String postId,
            String centerId,
            String centerName,
            String userProfile,
            String userNickname,
            Integer likeCount,
            List<HoldInfoResponseDto> holdList,
            String content,
            String createdAt,
            Boolean isDeleted,
            List<String> contentsList
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.centerName = centerName;
        this.userProfile = userProfile;
        this.userNickname = userNickname;
        this.likeCount = likeCount;
        this.holdList = holdList;
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
                post.getClimbingHistorySet().stream().map(climbingHistory ->
                        HoldInfoResponseDto.from(climbingHistory.getHoldInfo())).collect(Collectors.toList()),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentsSet().stream().map(PostContents::getUrl).collect(Collectors.toList())
        );
    }

    public static PostResponseDto from(Post post, List<String> postContentsList, List<HoldInfo> holdList) {
        return new PostResponseDto(
                post.getId(),
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                holdList.stream().map(HoldInfoResponseDto::from).collect(Collectors.toList()),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                postContentsList
        );
    }

    public static PostResponseDto from(Post post, Integer likeCount) {
        return new PostResponseDto(
                post.getId(),
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                likeCount,
                post.getClimbingHistorySet().stream().map(climbingHistory ->
                        HoldInfoResponseDto.from(climbingHistory.getHoldInfo())).collect(Collectors.toList()),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentsSet().stream().map(PostContents::getUrl).collect(Collectors.toList())
        );
    }
}
