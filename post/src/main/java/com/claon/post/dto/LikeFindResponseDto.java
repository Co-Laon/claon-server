package com.claon.post.dto;

import com.claon.post.domain.PostLike;
import lombok.Data;

@Data
public class LikeFindResponseDto {
    private String postId;
    private String likerId;

    private LikeFindResponseDto(
            String postId,
            String likerId
    ) {
        this.postId = postId;
        this.likerId = likerId;
    }

    public static LikeFindResponseDto from(PostLike postLike) {
        return new LikeFindResponseDto(
                postLike.getPost().getId(),
                postLike.getLikerId()
        );
    }
}
