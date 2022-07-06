package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentFindResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String postId;
    private final String writerId;
    private final String writerNickname;
    private final String writerProfile;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ChildCommentResponseDto> children;

    private CommentFindResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerId,
            String writerNickname,
            String writerProfile,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<ChildCommentResponseDto> children
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
        this.children = children;
    }

    public static CommentFindResponseDto from(PostComment postComment, List<PostComment> childComments) {
        return new CommentFindResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getWriter().getId(),
                postComment.getWriter().getNickname(),
                postComment.getWriter().getImagePath(),
                postComment.getCreatedAt(),
                postComment.getUpdatedAt(),
                childComments.stream().map(ChildCommentResponseDto::from).collect(Collectors.toList())
        );
    }
}
