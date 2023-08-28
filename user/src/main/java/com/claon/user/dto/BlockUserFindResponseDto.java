package com.claon.user.dto;

import com.claon.user.domain.BlockUser;
import lombok.Data;

@Data
public class BlockUserFindResponseDto {
    private final String blockUserNickName;

    private BlockUserFindResponseDto(
            String blockUserNickName
    ) {
        this.blockUserNickName = blockUserNickName;
    }

    public static BlockUserFindResponseDto from(BlockUser blockUser) {
        return new BlockUserFindResponseDto(
                blockUser.getBlockedUser().getNickname()
        );
    }
}
