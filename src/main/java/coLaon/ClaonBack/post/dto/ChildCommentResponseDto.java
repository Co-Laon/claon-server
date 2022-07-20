package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.common.utils.RelativeTimeUtil;
import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
public class ChildCommentResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String postId;
    private final String writerNickname;
    private final String writerProfileImage;
    private final String createdAt;
    private final String updatedAt;

    private ChildCommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerNickname,
            String writerProfileImage,
            String createdAt,
            String updatedAt
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.writerNickname = writerNickname;
        this.writerProfileImage = writerProfileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ChildCommentResponseDto from(PostComment postComment) {
        return new ChildCommentResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getWriter().getNickname(),
                postComment.getWriter().getImagePath(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getCreatedAt(), ZoneOffset.of("+9"))),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getUpdatedAt(), ZoneOffset.of("+9")))
        );
    }
}
