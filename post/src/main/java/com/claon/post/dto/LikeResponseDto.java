package com.claon.post.dto;

import com.claon.post.domain.PostLike;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LikeResponseDto {
    private final String postId;
    private final Integer likeCount;

    private LikeResponseDto(
            String postId,
            Integer likeCount
    ) {
        this.postId = postId;
        this.likeCount = likeCount;
    }

    public static LikeResponseDto from(
            PostLike like,
            Integer likeCount
    ) {
        return new LikeResponseDto(
                like.getPost().getId(),
                likeCount
        );
    }
}