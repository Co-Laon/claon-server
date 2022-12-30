package coLaon.ClaonBack.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoryGroupByMonthDto {
    private String date;
    private List<HistoryByCenterFindResponseDto> histories;

    private HistoryGroupByMonthDto(
            String date,
            List<HistoryByCenterFindResponseDto> histories
    ) {
        this.date = date;
        this.histories = histories;
    }

    public static HistoryGroupByMonthDto from(
            String date,
            List<HistoryByCenterFindResponseDto> histories
    ) {
        return new HistoryGroupByMonthDto(date, histories);
    }
}
