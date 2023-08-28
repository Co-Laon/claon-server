package com.claon.gateway.common.utils;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;

    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(this.SECRET_KEY).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
