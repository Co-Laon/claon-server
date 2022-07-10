package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

import java.util.Set;

@Data
public class PostResponseDto {
    private final String postId;
    private final String centerName;
    private final String holdName;
    private final String content;
    private final Boolean isDeleted;
    private final User writer;
    final Set<PostContents> contentsSet;

    private PostResponseDto(
            String postId,
            String centerName,
            String holdName,
            String content,
            Boolean isDeleted,
            User writer,
            Set<PostContents> contentsSet
    ) {
        this.postId = postId;
        this.centerName = centerName;
        this.holdName = holdName;
        this.content = content;
        this.isDeleted = isDeleted;
        this.writer = writer;
        this.contentsSet = contentsSet;
    }

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getCenterName(),
                post.getHoldInfo(),
                post.getContent(),
                post.getIsDeleted(),
                post.getWriter(),
                post.getContentsSet()
        );
    }
}
