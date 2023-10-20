package com.claon.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.core.EventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResilienceStateTransitionEventHandler implements EventConsumer<CircuitBreakerOnStateTransitionEvent> {

    @Override
    public void consumeEvent(CircuitBreakerOnStateTransitionEvent event) {
        CircuitBreaker.StateTransition stateTransition = event.getStateTransition();

        log.info(event.toString());
        log.info(stateTransition.toString());
    }
}
