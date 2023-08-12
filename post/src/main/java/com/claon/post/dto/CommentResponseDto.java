package com.claon.post.dto;

import com.claon.post.domain.PostComment;
import lombok.Data;

@Data
public class CommentResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String parentCommentId;
    private final String postId;

    private CommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String parentCommentId,
            String postId
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.parentCommentId = parentCommentId;
        this.postId = postId;
    }

    public static CommentResponseDto from(PostComment postComment) {
        return new CommentResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getParentComment() != null ? postComment.getParentComment().getId() : null,
                postComment.getPost().getId()
        );
    }
}
