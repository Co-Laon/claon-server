package coLaon.ClaonBack.user.dto;

import coLaon.ClaonBack.user.domain.Laon;
import lombok.Data;

@Data
public class LaonResponseDto {
    private final String laonId;
    private final String userId;

    private LaonResponseDto(
            String laonId,
            String userId
    ) {
        this.laonId = laonId;
        this.userId = userId;
    }

    public static LaonResponseDto from(
            Laon laon
    ) {
        return new LaonResponseDto(
                laon.getLaon().getId(),
                laon.getUser().getId()
        );
    }
}
