package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentFindResponseDto {
    private String commentId;
    private String content;
    private Boolean isDeleted;
    private String postId;
    private String writerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChildCommentResponseDto> children = new ArrayList<>();

    private CommentFindResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<ChildCommentResponseDto> children
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.writerId = writerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.children = children;
    }

    public static CommentFindResponseDto from(PostComment postComment, List<PostComment> childComments) {
        return new CommentFindResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getWriter().getId(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt(),
                childComments.stream().map(ChildCommentResponseDto::from).collect(Collectors.toList())
        );
    }
}
