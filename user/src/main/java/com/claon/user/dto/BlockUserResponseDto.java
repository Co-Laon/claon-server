package com.claon.user.dto;

import com.claon.user.domain.BlockUser;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlockUserResponseDto {
    private final String blockUserNickName;

    private BlockUserResponseDto(
            String blockUserNickName
    ) {
        this.blockUserNickName = blockUserNickName;
    }

    public static BlockUserResponseDto from(BlockUser blockUser) {
        return new BlockUserResponseDto(
                blockUser.getBlockedUser().getNickname()
        );
    }
}
