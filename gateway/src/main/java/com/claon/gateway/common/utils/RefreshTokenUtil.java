package com.claon.gateway.common.utils;

import com.claon.gateway.common.domain.RefreshToken;
import com.claon.gateway.common.exception.ErrorCode;
import com.claon.gateway.common.exception.UnauthorizedException;
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
        this.redisTemplate.opsForValue().set(refreshToken.getKey(), refreshToken.getUserId());
        this.redisTemplate.expire(refreshToken.getKey(), REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findByToken(String refreshKey) {
        String userId = this.redisTemplate.opsForValue().get(refreshKey);

        if (Objects.isNull(userId))
            return Optional.empty();

        return Optional.of(RefreshToken.of(refreshKey, userId));
    }

    public String delete(String token) {
        return this.findByToken(token).map(
                t -> {
                    this.redisTemplate.delete(t.getKey());
                    return t.getUserId();
                }
        ).orElseThrow(() -> new UnauthorizedException(
                ErrorCode.INVALID_JWT,
                "Redis 키를 찾을 수 없습니다."
        ));
    }
}
