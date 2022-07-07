package coLaon.ClaonBack.config.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDto {
    private final String accessToken;
    private final String refreshToken;
}
