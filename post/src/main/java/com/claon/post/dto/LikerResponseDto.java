package com.claon.post.dto;

import com.claon.post.domain.PostLike;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LikerResponseDto {
    private final String postId;
    private final String likerId;

    private LikerResponseDto(
            String postId,
            String likerId
    ) {
        this.postId = postId;
        this.likerId = likerId;
    }

    public static LikerResponseDto from(PostLike postLike) {
        return new LikerResponseDto(
                postLike.getPost().getId(),
                postLike.getLikerId()
        );
    }
}
