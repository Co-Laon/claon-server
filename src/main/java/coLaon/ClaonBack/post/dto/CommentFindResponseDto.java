package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.common.utils.RelativeTimeUtil;
import coLaon.ClaonBack.post.domain.PostComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentFindResponseDto {
    private final String commentId;
    private final String content;
    private final Boolean isDeleted;
    private final String postId;
    private final String writerNickname;
    private final String writerProfileImage;
    private final String createdAt;
    private final String updatedAt;
    private final List<ChildCommentResponseDto> children;
    private final Long commentCount;

    private CommentFindResponseDto(
            String commentId,
            String content,
            Boolean isDeleted,
            String postId,
            String writerNickname,
            String writerProfileImage,
            String createdAt,
            String updatedAt,
            List<ChildCommentResponseDto> children,
            Long commentCount
    ) {
        this.commentId = commentId;
        this.content = content;
        this.isDeleted = isDeleted;
        this.postId = postId;
        this.writerNickname = writerNickname;
        this.writerProfileImage = writerProfileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.children = children;
        this.commentCount = commentCount;
    }

    public static CommentFindResponseDto from(PostComment postComment, List<PostComment> childComments, Long commentCount) {
        return new CommentFindResponseDto(
                postComment.getId(),
                postComment.getContent(),
                postComment.getIsDeleted(),
                postComment.getPost().getId(),
                postComment.getWriter().getNickname(),
                postComment.getWriter().getImagePath(),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getCreatedAt(), ZoneOffset.of("+9"))),
                RelativeTimeUtil.convertNow(OffsetDateTime.of(postComment.getUpdatedAt(), ZoneOffset.of("+9"))),
                childComments.stream().map(ChildCommentResponseDto::from).collect(Collectors.toList()),
                commentCount
        );
    }
}
