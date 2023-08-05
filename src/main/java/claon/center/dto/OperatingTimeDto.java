package claon.center.dto;

import claon.center.domain.OperatingTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperatingTimeDto {
    private String day;
    private String start;
    private String end;

    public static OperatingTimeDto from(OperatingTime operatingTime) {
        return new OperatingTimeDto(
                operatingTime.getDay(),
                operatingTime.getStart(),
                operatingTime.getEnd()
        );
    }
}
