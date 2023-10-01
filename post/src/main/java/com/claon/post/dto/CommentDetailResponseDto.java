package com.claon.post.dto;

import com.claon.post.common.utils.RelativeTimeUtil;
import com.claon.post.domain.PostComment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@ToString
@NoArgsConstructor
public class CommentDetailResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;
    private String postId;
    private String writerId;
    private String createdAt;
    private String updatedAt;
    private Boolean isOwner;
    private Integer childrenCommentCount;

    @QueryProjection
    public CommentDetailResponseDto(
            PostComment postComment,
            Integer count,
            String userId
    ) {
        this.commentId = postComment.getId();
        this.content = postComment.getContent();
        this.isDeleted = postComment.getIsDeleted();
        this.postId = postComment.getPost().getId();
        this.writerId = postComment.getWriterId();
        this.createdAt = RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getCreatedAt(), ZoneOffset.of("+9")));
        this.updatedAt = RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getUpdatedAt(), ZoneOffset.of("+9")));
        this.childrenCommentCount = count;
        this.isOwner = postComment.getWriterId().equals(userId);
    }
}
