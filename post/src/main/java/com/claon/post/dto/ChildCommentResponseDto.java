package com.claon.post.dto;

import com.claon.post.common.utils.RelativeTimeUtil;
import com.claon.post.domain.PostComment;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@ToString
public class ChildCommentResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String postId;
    private final String writerId;
    private final String createdAt;
    private final String updatedAt;
    private final Boolean isOwner;

    private ChildCommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerId,
            String createdAt,
            String updatedAt,
            Boolean isOwner
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.writerId = writerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isOwner = isOwner;
    }

    public static ChildCommentResponseDto from(PostComment postComment, String userId) {
        return new ChildCommentResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getWriterId(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getCreatedAt(), ZoneOffset.of("+9"))),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getUpdatedAt(), ZoneOffset.of("+9"))),
                postComment.getWriterId().equals(userId)
        );
    }
}
