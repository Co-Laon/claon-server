package coLaon.ClaonBack.laon.dto;

import coLaon.ClaonBack.laon.domain.LaonComment;
import lombok.Data;

@Data
public class CommentResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;
    private String parentcommentId;
    private String laonId;

    public CommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String parentcommentId,
            String laonId
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.parentcommentId = parentcommentId;
        this.laonId = laonId;
    }

    public static CommentResponseDto from(LaonComment laonComment) {
        return new CommentResponseDto(
                laonComment.getId(),
                laonComment.getContent(),
                laonComment.getIsDeleted(),
                laonComment.getParentComment().getId(),
                laonComment.getLaon().getId()
        );
    }

}
