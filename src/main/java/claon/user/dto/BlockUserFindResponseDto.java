package claon.user.dto;

import claon.user.domain.BlockUser;
import lombok.Data;

@Data
public class BlockUserFindResponseDto {
    private final String blockUserNickName;
    private final String blockUserProfileImage;

    private BlockUserFindResponseDto(
            String blockUserNickName,
            String blockUserProfileImage
    ) {
        this.blockUserNickName = blockUserNickName;
        this.blockUserProfileImage = blockUserProfileImage;
    }

    public static BlockUserFindResponseDto from(BlockUser blockUser) {
        return new BlockUserFindResponseDto(
                blockUser.getBlockedUser().getNickname(),
                blockUser.getBlockedUser().getImagePath()
        );
    }
}
