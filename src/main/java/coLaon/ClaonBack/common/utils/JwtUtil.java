package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.config.dto.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${spring.jwt.access-token.cookie-name}")
    private String ACCESS_COOKIE_NAME;
    @Value("${spring.jwt.refresh-token.cookie-name}")
    private String REFRESH_COOKIE_NAME;
    @Value("${spring.jwt.access-token.expire-seconds}")
    private Integer ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${spring.jwt.refresh-token.expire-seconds}")
    private Integer REFRESH_TOKEN_EXPIRE_TIME;

    private final CookieUtil cookieUtil;

    @PostConstruct
    protected void init() {
        this.SECRET_KEY = Base64.getEncoder().encodeToString(this.SECRET_KEY.getBytes());
    }

    public JwtDto createToken(
            HttpServletResponse response,
            String userPk,
            Boolean isSignUp
    ) {
        Claims claims = Jwts.claims().setSubject(userPk);

        Date now = new Date();
        JwtDto jwtDto = new JwtDto(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + this.ACCESS_TOKEN_EXPIRE_TIME))
                        .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                        .compact(),
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + this.REFRESH_TOKEN_EXPIRE_TIME))
                        .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                        .compact());

        this.cookieUtil.createCookie(response, this.ACCESS_COOKIE_NAME, jwtDto.getAccessToken());
        this.cookieUtil.createCookie(response, this.REFRESH_COOKIE_NAME, jwtDto.getRefreshToken());
        this.cookieUtil.createCookie(response, "isSignUp", isSignUp.toString());

        return jwtDto;
    }

    public Authentication getAuthentication(String token) {
        String userPk = Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
        return new UsernamePasswordAuthenticationToken(userPk, null, new ArrayList<>());
    }

    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public JwtDto resolveToken(HttpServletRequest request) {
        return new JwtDto(
                this.cookieUtil.getCookie(request, this.ACCESS_COOKIE_NAME)
                        .orElse(null),
                this.cookieUtil.getCookie(request, this.REFRESH_COOKIE_NAME)
                        .orElse(null)
        );
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(jwtToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public void removeToken(HttpServletResponse response) {
        this.cookieUtil.removeCookie(response, this.ACCESS_COOKIE_NAME);
        this.cookieUtil.removeCookie(response, this.REFRESH_COOKIE_NAME);
    }
}
