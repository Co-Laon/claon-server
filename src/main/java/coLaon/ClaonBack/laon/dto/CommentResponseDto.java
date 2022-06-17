package coLaon.ClaonBack.laon.dto;

import coLaon.ClaonBack.laon.domain.LaonComment;
import lombok.Data;

@Data
public class CommentResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;

    public CommentResponseDto(
            String commentId,
            String content,
            Boolean isDeleted
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static CommentResponseDto from(LaonComment laonComment) {
        return new CommentResponseDto(
                laonComment.getId(),
                laonComment.getContent(),
                laonComment.getIsDeleted()
        );
    }

}
