package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.Center;
import lombok.Data;

@Data
public class CenterNameResponseDto {
    private String id;
    private String name;

    private CenterNameResponseDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CenterNameResponseDto from(Center center) {
        return new CenterNameResponseDto(center.getId(), center.getName());
    }
}
