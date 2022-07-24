package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.post.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.domain.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class IndividualUserResponseDto {
    private String nickname;
    private Long postCount;
    private Long laonCount;
    private Long likeCount;
    private String metropolitanActiveArea;
    private String basicLocalActiveArea;
    private String imagePath;
    private String instagramUrl;
    private Boolean isLaon;
    private Boolean isPrivate;
    private List<CenterClimbingHistoryResponseDto> centerClimbingHistories;


    private IndividualUserResponseDto(User user, boolean isLaon, Long postCount, Long laonCount, Long likeCount, List<ClimbingHistory> histories) {
        this.nickname = user.getNickname();
        this.isLaon = isLaon;
        this.postCount = postCount;
        this.laonCount = laonCount;
        this.likeCount = likeCount;
        this.isPrivate = user.getIsPrivate();
        this.imagePath = user.getImagePath();

        // Only set when private is false.
        this.processBlind(user);
        this.setHistoryDto(histories);
    }

    public static IndividualUserResponseDto from(User user, boolean isLaon, Long postCount, Long laonCount, Long likeCount, List<ClimbingHistory> histories) {
        return new IndividualUserResponseDto(user, isLaon, postCount, laonCount, likeCount, histories);
    }

    private void processBlind(User user) {
        if (!user.getIsPrivate()) {
            this.metropolitanActiveArea = user.getMetropolitanActiveArea();
            this.basicLocalActiveArea = user.getBasicLocalActiveArea();
            if (user.getInstagramUserName() != null) {
                this.instagramUrl = "https://instagram.com/" + user.getInstagramUserName();
            }
        }
    }

    private void setHistoryDto(List<ClimbingHistory> histories) {
        List<CenterClimbingHistoryResponseDto> result = new ArrayList<>();
        Map<String, List<ClimbingHistoryResponseDto>> historyMap = new HashMap<>();
        for (ClimbingHistory history : histories) {
            String centerName = history.getPost().getCenter().getName();
            historyMap.putIfAbsent(centerName, new ArrayList<>());
            List<ClimbingHistoryResponseDto> tempHistories = historyMap.get(centerName);
            tempHistories.add(new ClimbingHistoryResponseDto(history.getHoldInfo().getImg(), history.getClimbingCount()));
        }

        for (String centerName : historyMap.keySet()) {
            List<ClimbingHistoryResponseDto> tempHistories = historyMap.get(centerName);
            result.add(new CenterClimbingHistoryResponseDto(centerName, tempHistories));
        }
        this.centerClimbingHistories = result;
    }
}
