package claon.post.dto;

import claon.common.utils.RelativeTimeUtil;
import claon.post.domain.ClimbingHistory;
import claon.post.domain.Post;
import claon.post.domain.PostContents;
import claon.user.dto.ClimbingHistoryResponseDto;
import claon.user.dto.HoldInfoResponseDto;
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
    private final String content;
    private final Boolean isDeleted;
    private final String createdAt;
    private final List<String> contentsList;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private PostResponseDto(
            String postId,
            String centerId,
            String centerName,
            String userProfile,
            String userNickname,
            String content,
            String createdAt,
            Boolean isDeleted,
            List<String> contentsList,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.centerId = centerId;
        this.centerName = centerName;
        this.userProfile = userProfile;
        this.userNickname = userNickname;
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
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                post.getClimbingHistoryList().stream().map(
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

    public static PostResponseDto from(Post post, List<ClimbingHistory> climbingHistoryList) {
        return new PostResponseDto(
                post.getId(),
                post.getCenter().getId(),
                post.getCenter().getName(),
                post.getWriter().getImagePath(),
                post.getWriter().getNickname(),
                post.getContent(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(post.getCreatedAt(), ZoneOffset.of("+9"))),
                post.getIsDeleted(),
                post.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                climbingHistoryList.stream().map(
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
