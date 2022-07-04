package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentFindResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;
    private String postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentFindResponseDto> children = new ArrayList<>();

    private CommentFindResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentFindResponseDto from(PostComment postComment) {
        return new CommentFindResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt()
        );
    }

    public static CommentFindResponseDto discriminateIsDeleted(PostComment postComment) {
        return postComment.getIsDeleted() == Boolean.TRUE ?
                new CommentFindResponseDto(
                        postComment.getId(),
                        "삭제된 댓글입니다",
                        postComment.getIsDeleted(),
                        postComment.getPost().getId(),
                        postComment.getCreatedAt(),
                        postComment.getUpdatedAt()
                ) :
                new CommentFindResponseDto(
                        postComment.getId(),
                        postComment.getContent(),
                        postComment.getIsDeleted(),
                        postComment.getPost().getId(),
                        postComment.getCreatedAt(),
                        postComment.getUpdatedAt()
                );
    }
}
