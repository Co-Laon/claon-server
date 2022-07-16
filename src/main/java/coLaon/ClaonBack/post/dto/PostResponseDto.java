package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;


@Data
public class PostResponseDto {
    private final String postId;
    private final String centerName;
    private final String holdInfo;
    private final String content;
    private final Boolean isDeleted;
    private final List<String> contentsList;

    private PostResponseDto(
            String postId,
            String centerName,
            String holdInfo,
            String content,
            Boolean isDeleted,
            List<String> contentsList
    ) {
        this.postId = postId;
        this.centerName = centerName;
        this.holdInfo = holdInfo;
        this.content = content;
        this.isDeleted = isDeleted;
        this.contentsList = contentsList;
    }

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getCenterName(),
                post.getHoldInfo(),
                post.getContent(),
                post.getIsDeleted(),
                post.getContentsSet().stream().map(PostContents::getUrl).collect(Collectors.toList())
        );
    }

    public static PostResponseDto from(Post post, List<String> postContentsList) {
        return new PostResponseDto(
                post.getId(),
                post.getCenterName(),
                post.getHoldInfo(),
                post.getContent(),
                post.getIsDeleted(),
                postContentsList
        );
    }
}
