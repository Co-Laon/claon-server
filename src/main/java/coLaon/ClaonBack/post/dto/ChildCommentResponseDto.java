package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChildCommentResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String postId;
    private final String writerId;
    private final String writerNickname;
    private final String writerProfile;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ChildCommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerId,
            String writerNickname,
            String writerProfile,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.writerProfile = writerProfile;
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
                postComment.getWriter().getNickname(),
                postComment.getWriter().getImagePath(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt()
        );
    }
}
