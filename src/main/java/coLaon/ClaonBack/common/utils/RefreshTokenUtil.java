package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.common.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RefreshTokenUtil {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.jwt.refresh-token.expire-seconds}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    public void save(RefreshToken refreshToken) {
        this.redisTemplate.opsForValue().set(refreshToken.getToken(), refreshToken.getUserId());
        this.redisTemplate.expire(refreshToken.getToken(), REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findByToken(String refreshToken) {
        String userId = this.redisTemplate.opsForValue().get(refreshToken);

        if (Objects.isNull(userId))
            return Optional.empty();


        return Optional.of(RefreshToken.of(refreshToken, userId));
    }

    public void delete(String token) {
        this.findByToken(token).ifPresent(
                t -> this.redisTemplate.delete(t.getToken())
        );
    }
}
