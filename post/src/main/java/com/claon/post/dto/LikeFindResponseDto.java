package com.claon.post.dto;

import com.claon.post.domain.PostLike;
import lombok.Data;

@Data
public class LikeFindResponseDto {
    private String postId;
    private String likerId;
//    private String likerNickname;
//    private String likerProfileImage;

    private LikeFindResponseDto(
            String postId,
            String likerId
//            String likerNickname,
//            String likerProfileImage
    ) {
        this.postId = postId;
        this.likerId = likerId;
//        this.likerNickname = likerNickname;
//        this.likerProfileImage = likerProfileImage;
    }

    public static LikeFindResponseDto from(PostLike postLike) {
        return new LikeFindResponseDto(
                postLike.getPost().getId(),
                postLike.getLikerId()
//                postLike.getLiker().getNickname(),
//                postLike.getLiker().getImagePath()
        );
    }
}
