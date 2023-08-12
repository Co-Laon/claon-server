package com.claon.post.dto;

import com.claon.post.domain.PostLike;
import lombok.Data;

@Data
public class LikeResponseDto {
    private String postId;
    private Integer likeCount;

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