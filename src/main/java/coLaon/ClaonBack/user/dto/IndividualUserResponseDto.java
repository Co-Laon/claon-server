package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.center.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.post.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            List<ClimbingHistory> histories
    ) {
        this.nickname = user.getNickname();
        this.isLaon = isLaon;
        this.postCount = postCount;
        this.laonCount = laonCount;
        this.isPrivate = user.getIsPrivate();
        this.imagePath = user.getImagePath();

        // Only set when private is false.
        this.processBlind(user);
        this.setHistoryDto(histories);
    }

    public static IndividualUserResponseDto from(
            User user,
            Boolean isLaon,
            Long postCount,
            Long laonCount,
            List<ClimbingHistory> histories
    ) {
        return new IndividualUserResponseDto(
                user,
                isLaon,
                postCount,
                laonCount,
                histories
        );
    }

    private void processBlind(User user) {
        if (!user.getIsPrivate()) {
            this.height = user.getHeight();
            this.armReach = user.getArmReach();
            this.apeIndex = user.getArmReach() - user.getHeight();
            if (user.getInstagramUserName() != null) {
                this.instagramUrl = "https://instagram.com/" + user.getInstagramUserName();
            }
        }
    }

    private void setHistoryDto(List<ClimbingHistory> histories) {
        this.climbCount = histories.stream()
                .mapToLong(ClimbingHistory::getClimbingCount)
                .sum();

        Map<CenterPreviewResponseDto, Map<HoldInfoResponseDto, Integer>> historyMap = histories.stream().collect(
                Collectors.groupingBy(history -> CenterPreviewResponseDto.of(
                                history.getPost().getCenter().getThumbnailUrl(),
                                history.getPost().getCenter().getName()
                        ),
                        Collectors.toMap(
                                history -> HoldInfoResponseDto.from(history.getHoldInfo()),
                                ClimbingHistory::getClimbingCount,
                                Integer::sum
                        )
                ));

        this.centerClimbingHistories = historyMap.entrySet()
                .stream()
                .map(entry -> CenterClimbingHistoryResponseDto.from(
                        entry.getKey(),
                        entry.getValue().entrySet()
                                .stream()
                                .map(en -> ClimbingHistoryResponseDto.from(en.getKey(), en.getValue()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
