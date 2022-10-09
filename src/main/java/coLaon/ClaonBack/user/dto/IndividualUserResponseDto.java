package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class IndividualUserResponseDto {
    private String nickname;
    private Long postCount;
    private Long laonCount;
    private Long climbCount;
    private Float height;
    private Float armReach;
    private Float apeIndex;
    private String imagePath;
    private String instagramUrl;
    private Boolean isLaon;
    private Boolean isPrivate;
    private List<CenterClimbingHistoryResponseDto> centerClimbingHistories;

    private IndividualUserResponseDto(
            User user,
            Boolean isLaon,
            Long postCount,
            Long laonCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        this.nickname = user.getNickname();
        this.isLaon = isLaon;
        this.postCount = postCount;
        this.laonCount = laonCount;
        this.isPrivate = user.getIsPrivate();
        this.imagePath = user.getImagePath();
        this.climbCount = histories.stream()
                .map(CenterClimbingHistoryResponseDto::getClimbingHistories)
                .mapToLong(history ->
                        history.stream()
                                .mapToLong(ClimbingHistoryResponseDto::getClimbingCount)
                                .sum())
                .sum();

        // Only set when private is false.
        this.processBlind(user, histories);
    }

    public static IndividualUserResponseDto from(
            User user,
            Boolean isLaon,
            Long postCount,
            Long laonCount,
            List<CenterClimbingHistoryResponseDto> histories
    ) {
        return new IndividualUserResponseDto(
                user,
                isLaon,
                postCount,
                laonCount,
                histories
        );
    }

    private void processBlind(User user, List<CenterClimbingHistoryResponseDto> histories) {
        if (!user.getIsPrivate()) {
            this.height = user.getHeight();
            this.armReach = user.getArmReach();
            this.apeIndex = user.getArmReach() - user.getHeight();
            if (user.getInstagramUserName() != null) {
                this.instagramUrl = "https://instagram.com/" + user.getInstagramUserName();
            }
            this.centerClimbingHistories = histories;
        }
    }
}
