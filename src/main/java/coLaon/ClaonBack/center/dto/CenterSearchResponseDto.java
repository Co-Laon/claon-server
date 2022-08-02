package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.Center;
import lombok.Data;

@Data
public class CenterSearchResponseDto {
    private String id;
    private String name;

    private CenterSearchResponseDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CenterSearchResponseDto from(Center center) {
        return new CenterSearchResponseDto(center.getId(), center.getName());
    }
}
