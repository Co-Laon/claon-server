package coLaon.ClaonBack.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class HistoryByCenterFindResponseDto {
    private final String postId;
    private final String createdAt;
    private final List<ClimbingHistoryResponseDto> histories;

    private HistoryByCenterFindResponseDto(
            String postId,
            String createdAt,
            List<ClimbingHistoryResponseDto> histories
    ) {
        this.postId = postId;
        this.createdAt = createdAt;
        this.histories = histories;
    }

    public static HistoryByCenterFindResponseDto from(
            String postId,
            LocalDateTime createdAt,
            List<ClimbingHistoryResponseDto> climbingHistoryResponseDtoList
    ) {
        return new HistoryByCenterFindResponseDto(
                postId,
                createdAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                climbingHistoryResponseDtoList
        );
    }
}
