package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChildCommentResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;
    private String postId;
    private String writerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ChildCommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.writerId = writerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ChildCommentResponseDto from(PostComment postComment) {
        return new ChildCommentResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getWriter().getId(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt()
        );
    }
}
