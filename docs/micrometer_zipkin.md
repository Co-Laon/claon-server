## Micrometer, Zipkin

- Micrometer
  - actuator와 연동하여 metric 수집
  - 다양한 모니터링 시스템을 위해 instrumentation clients에 대한 간단한 애플리케이션 metric facade 제공
  - OpenZipkin Brave에 tracing bridge 제공 
- Zipkin
  - 분산 시스템 tracing을 통해 시각화, 모니터링 및 troubleshoot을 위한 open source 분산 추적 시스템
  - OpenTracing의 대표적인 구현체이며 HTTP와 gRPC 프로토콜 지원
- brave
  - W3C와 B3 trace context propagation 형식을 지원하는 분산 tracing instrumentation 라이브러리

### Spring Cloud Sleuth
- Spring Boot 3.0 이상에서 동작하지 않음
- Micrometer로 프로젝트 이전

### Metrics
- `/actuator/metrics/{metricName}`
  - Tag 필터 기능 제공
    - key:value 형식으로 필터링
    - 힙 메모리 확인
      - `/actuator/metrics/jvm.memory.used?tag=area:heap`
    - 힙이 아닌 메모리 확인
      - `/actuator/metrics/jvm.memory.used?tag=area:nonheap`
    - log 요청과 HTTP Status 200 필터링
      - `/actuator/metrics/http.server.requests?tag=uri:/log&tag=status:200`
  - JVM Metric
    - `jvm.`
    - 메모리와 버퍼 풀 세부 정보, 가비지 수집 관련 통계, 스레드 활용, 로드 및 언로드된 클래스수, JVM 버전 정보, JIT 컴파일 시간 등 확인
  - System Metric
    - `system.` `process.` `disk.`
    - CPU 지표, 파일 디스크립터, 가동 시간, 사용 가능한 디스크 공간 등 확인
  - Application Startup Metric
    - `application.started.time`
      - 애플리케이션을 시작 소요 시간
      - ApplicationStartedEvent로 측정
    - `application.ready.time`
      - 애플리케이션이 준비가 되는데 걸리는 시간
      - ApplicationReadyEvent로 측정
  - Spring MVC Metric
    - `http.server.requests`
    - 스프링 MVC 컨트롤러가 처리하는 모든 요청
    - Tag를 사용하여 URI, method, status, exception, outcome 등을 확인
  - DataSource Metric
    - `jdbc.connections`
      - DataSource와 connection pool에 관한 Metric 확인
      - 최대 커넥션, 최소 커넥션, 활성 커넥션, 대기 커넥션 수 등을 확인
    - `hikaricp.`
      - hikari connection pool에 대한 자세한 Metric 확인 가능
  - 로그 Metric
    - `logback.event:logback` 
    - 로그에 대한 Metric을 확인
    - trace, debug, info, warn, error 로그 레벨에 따른 로그 수 확인 가능
  - tomcat Metric
    - `tomcat.`
      ```yaml
      server:
        tomcat:
          mbeanregistry:
            enabled: true
      ```
    - 옵션을 키지 않을 경우 `tomcat.session.` 정보만 노출
    - tomcat의 최대 스레드, 사용 스레드 수 등을 확인
  - [more](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics.supported)

### Tracing Propagation
- `default` W3C propagation
  - traceparent, tracestate 헤더를 전달
- B3 propagation
  - Single Header
    - b3: {TraceId}-{SpanId}-{SamplingState}-{ParentSpanId} 형식으로 전달
  - Multiple Header
    - x-b3-traceid, x-b3-spanid, x-b3-sampled, x-b3-parentspanid 헤더를 전달

### Config
- 의존성 추가
  ```
  implementation 'org.springframework.boot:spring-boot-starter-actuator'

  implementation "io.micrometer:micrometer-tracing"
  implementation "io.micrometer:micrometer-tracing-bridge-brave"
  implementation "io.zipkin.reporter2:zipkin-reporter-brave"
  ```

  | Property                     | Default | Description                      |
  |------------------------------|---------|----------------------------------|
  | tracing.sampling.probability | 0.1     | 샘플링할 로그의 비율                      |
  | tracing.propagation.consume  | 100     | 외부에서 보내는 요청의 Trace Context 해석 방식 |
  | tracing.propagation.produce  | 60000   | 외부로 보내는 요청의 Trace Context 해석 방식  |
  | zipkin.tracing.endpoint      |         | 로그를 전송할 endpoint                 |

- Example
  ```yaml
  management:
    tracing:
      sampling:
        probability: 1.0
      propagation:
        consume: b3_single
        produce: b3_multi
    zipkin:
      tracing:
        endpoint: "http://localhost:9411/api/v2/spans"
  
  logging:
    pattern:
      level: "%5p [%X{traceId:-},%X{spanId:-}]"
  ```

  ```java
  @Configuration
  @Import({BraveAutoConfiguration.class,
          MicrometerTracingAutoConfiguration.class,
          ObservationAutoConfiguration.class})
  public class TraceConfig {}
  ```