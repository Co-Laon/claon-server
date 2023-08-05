package claon.post.dto;

import claon.common.utils.RelativeTimeUtil;
import claon.post.domain.PostComment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
public class CommentFindResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String postId;
    private final String writerNickname;
    private final String writerProfileImage;
    private final String createdAt;
    private final String updatedAt;
    private final Boolean isOwner;
    private final Integer childrenCommentCount;

    @QueryProjection
    public CommentFindResponseDto(
            PostComment postComment,
            Integer count,
            String userNickname
    ) {
        this.commentId = postComment.getId();
        this.content = postComment.getContent();
        this.isDeleted = postComment.getIsDeleted();
        this.postId = postComment.getPost().getId();
        this.writerNickname = postComment.getWriter().getNickname();
        this.writerProfileImage = postComment.getWriter().getImagePath();
        this.createdAt = RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getCreatedAt(), ZoneOffset.of("+9")));
        this.updatedAt = RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getUpdatedAt(), ZoneOffset.of("+9")));
        this.childrenCommentCount = count;
        this.isOwner = postComment.getWriter().getNickname().equals(userNickname);
    }
}
