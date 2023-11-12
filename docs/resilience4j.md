## Resilience4j

- Functional Programming을 위해 설계된 fault tolerance를 위한 경량 라이브러리
- Circuit Breaker, Bulkhead, RateLimiter, Retry 등을 지원

- 의존성 추가
  ```groovy
  // Spring Boot 3
  implementation "io.github.resilience4j:resilience4j-spring-boot3"
  // Circuit Breaker
  implementation "org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j"
  // Circuit Breaker Reactive version
  implementation "org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j"
  ```

### Spring Cloud Netflix Hystrix
- Spring Boot 2.4 이상에서 [Maintenance Mode](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode) 인입
- Spring Boot 3.0 이상에서 동작하지 않음
- Resilience4j와 차이점
  - Hystrix는 외부 시스템에 대한 호출을 HystrixCommand로 감싸는 것이 필요 <br/>
    Resilience4j는 고차함수(데코레이터) 제공하여, 둘 이상의 기능을 같이 사용 가능
  - Hystrix는 half-open 상태에서 한 번의 호출로 Circuit을 닫을 지 결정 <br/>
    Resilience4j는 half-open 상태에서 호출 수와 임계값으로 Circuit을 닫음
  - Hystrix는 Guava 및 Apache Commons와 같은 외부 종속성이 많은 Archaius 종속성이 있음 <br/>
    Resilience4j는 Vavr만 사용하고 다른 외주 종속성이 없기에 가벼움

### Circuit Breaker Pattern
- 장애를 방지하기 위한 것으로, 실패할 수 있는 요청을 계속 시도하지 않도록 방지함
- Circuit의 상태는 open, closed, half-open으로 구분

<img src="./images/resiliency-pattern-circuit-breaker-flow.png" width="350">

- 장점
  - 장애 감지 및 격리
    - 만약 장애가 발생한 서비스를 호출할 때, 요청이 타임아웃만큼 대기하며 자원(쓰레드)을 점유 <br/> 
      자원 부족으로 인해 호출한 서비스로 장애 전파
    - 장애가 발생한 서비스를 감지하고, 요청을 차단함으로써 장애 격리
  - 자동 시스템 복구
    - 조건에 따라 자동으로 Circuit 상태 변경
    - 모니터링 기능 제공으로 시스템들의 현황 파악 가능
  - 장애 서비스로의 부하 감소

 Config

  | Property                              | Default | Description                                     |
  |---------------------------------------|---------|-------------------------------------------------|
  | slidingWindowSize                     | 100     | closed 상태에서 호출 결과를 기록하는 데 사용되는 slidingWindow 크기 |
  | minimumNumberOfCalls                  | 100     | error rate 또는 slow call rate를 계산하는데 필요한 최소 요청 수 |
  | waitDurationInOpenState               | 60000   | open에서 half-open으로 전환 되기 전 대기 시간                |
  | registerHealthIndicator               | false   | /actuator/health로 circuit 상태 표시                 |
  | permittedNumberOfCallsInHalfOpenState | 10      | half-open 상태에서 허용되는 호출 수                        |
  | recordExceptions                      |         | Circuit이 기록해야할 예외 목록                            |
 
  [more](https://resilience4j.readme.io/docs/circuitbreaker#decorate-and-execute-a-functional-interface)

- Example
  - for Spring Cloud Gateway
  ```yml
  management:
    endpoint:
      health:
        show-details: always
    health:
      circuitbreakers:
        enabled: true
  resilience4j.circuitbreaker:
    configs:
      default:
        minimum-number-of-calls: 5
        slidingWindowSize: 10
        permittedNumberOfCallsInHalfOpenState: 5
        waitDurationInOpenState: 10000
        registerHealthIndicator: true
        record-exceptions:
          - java.lang.Exception
    instances:
      CB-GATEWAY:
        base-config: default
  
  spring:
    cloud:
      gateway:
        default-filters:
          - name: CircuitBreaker
            args:
              name: CB-GATEWAY
              fallbackUri: forward:/fallback
              statusCodes:
                - 500
  ```
  
  ```java
  @Configuration
  public class Resilience4jConfig {
    @Bean
    public ReactiveResilience4JCircuitBreakerFactory defaultCustomizer(
        CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        Set<CircuitBreaker> circuits = circuitBreakerRegistry.getAllCircuitBreakers();
        // Consume emitted CircuitBreakerEvents
        circuits.forEach(c -> c.getEventPublisher().onStateTransition(new ResilienceStateTransitionEventHandler()));
  
        return new ReactiveResilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry);
    }
  }
  ```
  - for Feign Client
  ```java
  @FeignClient(name = "post-service")
  public interface PostClient {
    @CircuitBreaker(name = "CB-GATEWAY")
    @GetMapping("/api/v1/histories")
    List<UserPostInfoResponse> findHistoriesByUserId(@RequestHeader(name = "X-USER-ID") String userId);
  }
  ```

### Bulkhead Pattern
- 서비스가 자원을 분리하고 격리함으로써 계단식 오류를 방지하는데 사용되는 패턴
- thread, connection과 같은 자원을 특정 요소에 할당할 수 있는 별도의 pool로 분할
- Resilience4j는 2가지 구현체 제공
  - SemaphoreBulkhead
    - 세마포어를 이용하여 동시 요청을 제한하는 방법
  - FixedThreadPoolBulkhead
    - thread pool을 이용하여 동시 요청을 제한하는 방법

- Config
  - SemaphoreBulkhead

    | Property           | Default | Description                           |
    |--------------------|---------|---------------------------------------|
    | maxConcurrentCalls | 25      | 최대로 허용할 병렬 실행 수                       |
    | maxWaitDuration    | 0       | 포화상태일 때 진입하려는 thread가 블로킹되어야 하는 최대 시간 |

    ```yml
    resilience4j:
      bulkhead:
        configs:
          default:
            maxWaitDuration: 5000
            maxConcurrentCalls: 5
    ```

  - FixedThreadBulkHead
   
    | Property                  | Default                                        | Description                                                      |
    |---------------------------|------------------------------------------------|------------------------------------------------------------------|
    | maxThreadPoolSize         | Runtime.getRuntime().availableProcessors()     | thread pool의 최대 크기                                               |
    | coreThreadPoolSize        | Runtime.getRuntime().availableProcessors() - 1 | core thread pool의 크기                                             |
    | queCapacity               | 100                                            | 큐 용량                                                             |
    | keepAliveDuaration        | 20                                             | thread 수가 core보다 많을 때, 초과 idle thread가 종료 되기 전에 새 작업을 기다리는 최대 시간 |
    | writableStackTraceEnabled | true                                           | BulkheadFullException이 발생할 때 StackTrace 출력 여부                    |

    ```yml
    resilience4j:
      thread-pool-bulkhead:
        configs:
          default:
            maxThreadPoolSize: 4
            coreThreadPoolSize: 3
            queueCapacity: 8
    ```

- Example
  - SemaphoreBulkhead
  ```yml
  resilience4j:
    bulkhead:
      instances:
        BH-GATEWAY:
          base-config: default
  ```
  
  ```java
  @FeignClient(name = "post-service")
  public interface PostClient {
    @Bulkhead(name = "BH-GATEWAY", type = Bulkhead.Type.SEMAPHORE)
    @GetMapping("/api/v1/histories")
    List<UserPostInfoResponse> findHistoriesByUserId(@RequestHeader(name = "X-USER-ID") String userId);
  }
  ```
  - FixedThreadPoolBulkhead
  ```yml
    resilience4j:
        thread-pool-bulkhead:
        instances:
          BH-GATEWAY:
            base-config: default
  ```
    
  ```java
  @FeignClient(name = "post-service")
  public interface PostClient {
    @Bulkhead(name = "BH-GATEWAY", type = Bulkhead.Type.THREADPOOL)
    @GetMapping("/api/v1/histories")
    List<UserPostInfoResponse> findHistoriesByUserId(@RequestHeader(name = "X-USER-ID") String userId);
  }
  ```

### RateLimiter Pattern
- 단위 시간동안 요청 실행을 제한하는 패턴
- 초과 요청에 대해 거부하거나 Queue에 대기시키는 방법으로 처리 가능

- Config
  
  | Config Property    | Default Value | Description                            |
  |--------------------|---------------|----------------------------------------|
  | timeoutDuration    | 5s            | thread가 권한을 위해 기다리는 default 시간         |
  | limitRefreshPeriod | 500ns         | limitForPeriod 값이 갱신되는 주기              |
  | limitForPeriod     | 50            | limitRefreshPeriod에 설정된 시간동안 허용되는 요청 수 |

  ```yml
  resilience4j: 
    ratelimiter:
      configs:
        default:
          limitForPeriod: 3
          limitRefreshPeriod: 4s
          timeoutDuration: 10s
  ```
- Example
  ```yml
    resilience4j:
      ratelimiter:
        instances:
          RL-GATEWAY:
            base-config: default
  ```
    
  ```java
  @FeignClient(name = "post-service")
  public interface PostClient {
    @RateLimiter(name = "RL-GATEWAY")
    @GetMapping("/api/v1/histories")
    List<UserPostInfoResponse> findHistoriesByUserId(@RequestHeader(name = "X-USER-ID") String userId);
  }
  ```

### TimeLimiter Pattern
- Timeout을 설정하여, 지정된 시간 내에 작업이 완료되지 않으면 작업을 취소하는 패턴
- 비동기 작업에만 적용 가능 (CompletableFuture 반환 필요)

- Config
  
  | Config Property     | Default Value | Description                   |
  |---------------------|---------------|-------------------------------|
  | cancelRunningFuture | true          | timeout이 경과한 후, Future의 취소 여부 |
  | timeoutDuration     | 1000ms        | timeout 시간 설정                 |
  
  > cancelRunningFuture가 False일 경우, 작업을 취소하지 않고 완료될 때까지 실행
  
  ```yml
  resilience4j:
    timelimiter:
      configs:
        default:
          cancelRunningFuture: true
          timeoutDuration: 2s
  ```

- Example
  ```yml
  resilience4j:
    timelimiter:
      instances:
        TL-GATEWAY:
          base-config: default
  ```
        
  ```java
  @TimeLimiter(name = "TL-GATEWAY")
  // ...
  ```
