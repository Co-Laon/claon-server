package com.claon.gateway.config;

import com.claon.gateway.common.domain.JwtDto;
import com.claon.gateway.common.exception.ErrorCode;
import com.claon.gateway.common.utils.HeaderUtil;
import com.claon.gateway.common.utils.JwtUtil;
import com.claon.gateway.common.utils.RefreshTokenUtil;
import com.claon.gateway.domain.UserDetails;
import com.claon.gateway.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final HeaderUtil headerUtil;
    private final RefreshTokenUtil refreshTokenUtil;

    private final UserRepository userRepository;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        JwtDto jwtDto = this.headerUtil.resolveToken(request);

        if (jwtDto.getAccessToken() == null || jwtDto.getRefreshToken() == null) {
            // no tokens
            request.setAttribute("exception", ErrorCode.NOT_SIGN_IN);
            chain.doFilter(request, response);
            return;
        }

        if (this.jwtUtil.validateToken(jwtDto.getAccessToken())) {
            if (this.jwtUtil.validateToken(jwtDto.getRefreshToken())) {
                // Success sign-in because access token and refresh token are valid
                this.getAuthentication(this.jwtUtil.getUserId(jwtDto.getAccessToken())).ifPresentOrElse(
                        auth -> SecurityContextHolder.getContext().setAuthentication(auth),
                        () -> request.setAttribute("exception", ErrorCode.USER_DOES_NOT_EXIST)
                );
            } else {
                // Fail sign in because refresh token is invalid format or expired
                request.setAttribute("exception", ErrorCode.INVALID_JWT);
            }
        } else if (this.jwtUtil.isExpiredToken(jwtDto.getAccessToken())) {
            if (this.jwtUtil.validateToken(jwtDto.getRefreshToken())) {
                // Success sign-in because refresh token is valid and access token is valid format but expired
                // Reissue access and refresh token
                this.refreshTokenUtil.findByToken(jwtDto.getRefreshToken()).ifPresentOrElse(
                        token -> {
                            JwtDto newToken = this.jwtUtil.reissueToken(jwtDto.getRefreshToken(), token.getUserId());
                            this.headerUtil.addToken(response, newToken);

                            this.getAuthentication(this.jwtUtil.getUserId(newToken.getAccessToken())).ifPresentOrElse(
                                    auth -> SecurityContextHolder.getContext().setAuthentication(auth),
                                    () -> request.setAttribute("exception", ErrorCode.USER_DOES_NOT_EXIST)
                            );
                        },
                        () -> request.setAttribute("exception", ErrorCode.INVALID_JWT)
                );
            } else {
                // Fail sign in because refresh token is invalid format or expired
                request.setAttribute("exception", ErrorCode.INVALID_JWT);
            }
        } else {
            // Fail sign in because access token is invalid format
            request.setAttribute("exception", ErrorCode.INVALID_JWT);
        }

        chain.doFilter(request, response);
    }

    private Optional<UsernamePasswordAuthenticationToken> getAuthentication(String userPk) {
        return this.userRepository.findById(userPk)
                .map(user -> new UsernamePasswordAuthenticationToken(new UserDetails(user), null, new ArrayList<>()));
    }
}
