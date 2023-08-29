package com.claon.gateway.config;

import com.claon.gateway.common.utils.HeaderUtil;
import com.claon.gateway.common.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthenticationHeaderFilter extends AbstractGatewayFilterFactory<AuthenticationHeaderFilter.Config> {
    private final HeaderUtil headerUtil;
    private final JwtUtil jwtUtil;

    public AuthenticationHeaderFilter(HeaderUtil headerUtil, JwtUtil jwtUtil) {
        super(Config.class);
        this.headerUtil = headerUtil;
        this.jwtUtil = jwtUtil;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Authentication Filter...");

            String token = this.headerUtil.resolveAccessToken(exchange.getRequest());
            String userId = this.jwtUtil.getUserId(token);

            log.info("Request Client Id : " + userId);

            addAuthorizationHeaders(exchange.getRequest(), userId);

            return chain.filter(exchange);
        };
    }

    private void addAuthorizationHeaders(ServerHttpRequest request, String userId) {
        request.mutate()
                .header("X-USER-ID", userId)
                .build();
    }

    @Bean
    public ErrorWebExceptionHandler tokenValidation() {
        return new JwtTokenExceptionHandler();
    }

    public static class JwtTokenExceptionHandler implements ErrorWebExceptionHandler {
        private String getErrorCode(int errorCode) {
            return "{\"errorCode\":" + errorCode + "}";
        }

        @Override
        public Mono<Void> handle(
                ServerWebExchange exchange, Throwable ex
        ) {
            int errorCode = 500;
            if (ex.getClass() == NullPointerException.class) {
                errorCode = 100;
            } else if (ex.getClass() == ExpiredJwtException.class) {
                errorCode = 200;
            }

            byte[] bytes = getErrorCode(errorCode).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }

}

