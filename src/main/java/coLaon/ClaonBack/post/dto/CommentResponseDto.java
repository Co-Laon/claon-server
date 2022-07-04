package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

@Data
public class CommentResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;
    private String parentCommentId;
    private String laonId;

    private CommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String parentCommentId,
            String laonId
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.parentCommentId = parentCommentId;
        this.laonId = laonId;
    }

    public static CommentResponseDto from(PostComment postComment) {
        return new CommentResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getParentComment()!= null ? postComment.getParentComment().getId() : null,
                postComment.getPost().getId()
        );
    }

}
