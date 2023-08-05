package claon.user.dto;

import lombok.Data;

@Data
public class DuplicatedCheckResponseDto {
    private Boolean result;

    private DuplicatedCheckResponseDto(boolean result) {
        this.result = result;
    }

    public static DuplicatedCheckResponseDto of(boolean result) {
        return new DuplicatedCheckResponseDto(result);
    }
}
