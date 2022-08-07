package coLaon.ClaonBack.version.dto;


import coLaon.ClaonBack.version.domain.AppVersion;
import lombok.Data;

@Data
public class AppVersionFindResponseDto {
    private final String key;
    private final String version;

    private AppVersionFindResponseDto(
            String key,
            String version
    ) {
        this.key = key;
        this.version = version;
    }

    public static AppVersionFindResponseDto from(AppVersion appVersion) {
        return new AppVersionFindResponseDto(
                appVersion.getKey(),
                appVersion.getValue()
        );
    }

}
