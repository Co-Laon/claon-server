package com.claon.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class Resilience4jConfig {
    @Bean
    public ReactiveResilience4JCircuitBreakerFactory defaultCustomizer(
            CircuitBreakerRegistry circuitBreakerRegistry,
            TimeLimiterRegistry timeLimiterRegistry
    ) {
        Set<CircuitBreaker> circuits = circuitBreakerRegistry.getAllCircuitBreakers();
        circuits.forEach(c -> c.getEventPublisher().onStateTransition(new ResilienceStateTransitionEventHandler()));

        return new ReactiveResilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry);
    }
}
