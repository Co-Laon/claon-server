package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.common.domain.JwtDto;
import coLaon.ClaonBack.common.domain.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final RefreshTokenUtil refreshTokenUtil;

    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${spring.jwt.access-token.expire-seconds}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${spring.jwt.refresh-token.expire-seconds}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    @PostConstruct
    protected void init() {
        this.SECRET_KEY = Base64.getEncoder().encodeToString(this.SECRET_KEY.getBytes());
    }

    public JwtDto createToken(
            String userPk,
            Boolean isCompletedSignUp
    ) {
        Date now = new Date();
        return JwtDto.of(
                generateAccessToken(userPk, now),
                generateRefreshToken(userPk, now),
                isCompletedSignUp);
    }

    public JwtDto reissueToken(
            String refreshToken,
            String userPk
    ) {
        this.refreshTokenUtil.delete(refreshToken);

        Date now = new Date();
        JwtDto newToken = JwtDto.of(
                generateAccessToken(userPk, now),
                generateRefreshToken(userPk, now));

        this.refreshTokenUtil.save(RefreshToken.of(newToken.getRefreshToken(), userPk));

        return newToken;
    }

    private String generateAccessToken(String userPk, Date now) {
        Claims claims = Jwts.claims().setSubject(userPk);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + this.ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                .compact();
    }

    private String generateRefreshToken(String userPk, Date now) {
        String token = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + this.REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                .compact();

        this.refreshTokenUtil.save(RefreshToken.of(token, userPk));

        return token;
    }

    public void deleteRefreshToken(String refreshToken) {
        this.refreshTokenUtil.delete(refreshToken);
    }

    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(jwtToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExpiredToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(jwtToken);

            return claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
