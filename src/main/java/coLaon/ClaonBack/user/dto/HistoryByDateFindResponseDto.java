package coLaon.ClaonBack.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoryByDateFindResponseDto {
    CenterInfoResponseDto centerInfo;
    List<ClimbingHistoryResponseDto> histories;

    private HistoryByDateFindResponseDto(
            CenterInfoResponseDto centerInfo,
            List<ClimbingHistoryResponseDto> histories) {
        this.centerInfo = centerInfo;
        this.histories = histories;
    }

    public static HistoryByDateFindResponseDto from(
            CenterInfoResponseDto centerInfo,
            List<ClimbingHistoryResponseDto> histories
    ) {
        return new HistoryByDateFindResponseDto(centerInfo, histories);
    }
}
