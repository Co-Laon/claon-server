package com.claon.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
public class FallbackController {
    @GetMapping("/fallback")
    public Flux<Void> getFallback() {
        log.info("Fallback for gateway");
        return Flux.empty();
    }
}
