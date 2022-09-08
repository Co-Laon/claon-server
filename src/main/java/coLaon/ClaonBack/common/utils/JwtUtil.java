package coLaon.ClaonBack.common.utils;

import coLaon.ClaonBack.config.UserAccount;
import coLaon.ClaonBack.config.dto.JwtDto;
import coLaon.ClaonBack.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${spring.jwt.access-token.expire-seconds}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${spring.jwt.refresh-token.expire-seconds}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;

    private final UserRepository userRepository;

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

    public JwtDto createToken(
            String userPk
    ) {
        Date now = new Date();
        return JwtDto.of(
                generateAccessToken(userPk, now),
                generateRefreshToken(userPk, now));
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
        Claims claims = Jwts.claims().setSubject(userPk);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + this.REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                .compact();
    }

    public Optional<UsernamePasswordAuthenticationToken> getAuthentication(String token) {
        String userPk = Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(token).getBody().getSubject();

        return this.userRepository.findById(userPk)
                .map(user -> new UsernamePasswordAuthenticationToken(new UserAccount(user), null, new ArrayList<>()));
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
}
