package coLaon.ClaonBack.config;

import coLaon.ClaonBack.common.utils.HeaderUtil;
import coLaon.ClaonBack.user.domain.UserDetails;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.utils.JwtUtil;
import coLaon.ClaonBack.common.domain.JwtDto;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@AllArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final HeaderUtil headerUtil;

    private final UserRepository userRepository;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        JwtDto jwtDto = this.headerUtil.resolveToken((HttpServletRequest) request);

        if (jwtDto.getAccessToken() != null && jwtDto.getRefreshToken() != null) {
            if (this.jwtUtil.validateToken(jwtDto.getAccessToken())) {
                if (this.jwtUtil.validateToken(jwtDto.getRefreshToken())) {
                    // Success sign-in
                    this.getAuthentication(this.jwtUtil.getUserId(jwtDto.getAccessToken())).ifPresentOrElse(
                            authentication -> SecurityContextHolder.getContext().setAuthentication(authentication),
                            () -> request.setAttribute("exception", ErrorCode.USER_DOES_NOT_EXIST)
                    );
                } else {
                    // Fail sign in because expire refresh token
                    request.setAttribute("exception", ErrorCode.INVALID_JWT);
                }
            } else {
                if (this.jwtUtil.validateToken(jwtDto.getRefreshToken())) {
                    // Success sign-in and create access and refresh token
                    String userId = this.jwtUtil.getUserId(jwtDto.getRefreshToken());

                    JwtDto newToken = this.jwtUtil.createToken(userId);
                    this.headerUtil.addToken((HttpServletResponse) response, newToken);

                    this.getAuthentication(this.jwtUtil.getUserId(newToken.getAccessToken())).ifPresentOrElse(
                            authentication -> SecurityContextHolder.getContext().setAuthentication(authentication),
                            () -> request.setAttribute("exception", ErrorCode.USER_DOES_NOT_EXIST)
                    );
                } else {
                    // Fail sign in because expire access and refresh token
                    request.setAttribute("exception", ErrorCode.INVALID_JWT);
                }
            }
        } else {
            // no tokens
            request.setAttribute("exception", ErrorCode.NOT_SIGN_IN);
        }

        chain.doFilter(request, response);
    }

    private Optional<UsernamePasswordAuthenticationToken> getAuthentication(String userPk) {
        return this.userRepository.findById(userPk)
                .map(user -> new UsernamePasswordAuthenticationToken(new UserDetails(user), null, new ArrayList<>()));
    }
}
