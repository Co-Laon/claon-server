### 1. Micrometer-brave + Zipkin 개요
- 모놀리식 구조는 하나의 서버가 서비스의 전반적이 기능을 모두 제공하므로 클라이언트의 요청을 받으면 하나의 스레드에서 모든 요청을 실행하여 로그를 확인하기 쉽다.
- MSA 는 여러개의 마이크로 서비스 간에 통신이 발생하기 때문에 로그를 확인하기 어려우며 이러한 분산추적을 위한 표준으로 OpenTracing이 알려져 있다.
    - Zipkin은 Opentracing의 대표적인 구현체이며 추적할 수 있는 분산 트랜잭션은 HTTP와 gRPC가 있음
- Micrometer는 애플리케이션 메트릭 파사로 애플리케이션의 측정 지표(metric)를 Micrometer가 정한 표준 방법으로 모아서 제공한다.
    - 추상화를 통해 구현체를 쉽게 변경할 수 있도록 구성
    - Actuator는 Micrometer를 기본으로 내장해 사용
    - Spring Boot3부터 Sleuth는 지원하지 않고 Micrometer로 이전

### 2. Usage Setting
- 의존성 추가
    - actuator 의존성이 반드시 필요
```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    implementation "io.micrometer:micrometer-tracing"
    implementation "io.micrometer:micrometer-tracing-bridge-brave"
    implementation "io.zipkin.reporter2:zipkin-reporter-brave"
```
- Property 설정
    - Zipkin에서 서비스를 구분할 application-name 지정
    - 샘플링할 비율인 probability 지정
        - 기본값은 0.1이며 모든 로그를 샘플링할 경우에는 1.0
    - Trace Context를 다른 서비스로 전파할 때 HTTP 헤더에 어떤 이름을 사용해 정보를 전송할 지 지정
        - propagation의 consume과 produce 값으로 지정이 가능
            - consume : 외부 서비스에서 현재 서비스로 보내는 요청의 Trace Context를 해석하는 방식
            - produce : 현재 서비스에서 외부 서비스로 요청하는 Trace Context를 해석하는 방식
        - 기본값은 W3C propagation이며 B3 Single header와 B3 multiple header가 있음
    - 로그를 전송할 tracing.endpoint 설정
    - 로컬 머신의 로그에서 traceID와 spanID를 확인하기 위해 logging.pattern.level을 설정
        - traceID, spanID, parentID, sampled 확인이 가능
```yaml
server:
  port: { port }

spring:
  application:
    name: { application-name }

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
### 3. Zipkin 구조
![구조](https://zipkin.io/public/img/architecture-1.png)
- Reporter가 transport를 통해 Collector에 추적 정보를 전달한다.
- 전달된 추적 정보는 Database에 저장된다.
    - 일반적으로 In-memory DB는 테스트 목적, 소규모 프로젝트에서는 MySQL을 사용하며 운영환경에서는 ElasticSearch나 Cassandra를 사용
- API는 Web UI의 질의를 받아 Storage를 검색하여 결과를 반환한다.
- UI는 대시보드 WEB UI를 제공한다
- Zipkin 실행시 아래와 같은 Sequence를 가진다.
  ![Zipkin-Seq](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FEPtyy%2FbtrVA1DCiwT%2F9sRWKvkEH96i5SlHVzWKzk%2Fimg.png)


- Metrics Endpoint
    - 기본 제공 Metric 확인
    - `/actuator/metrics/{metricName}`
    - Tag 필터 기능 제공
        - KEY:VALUE 형식으로 필터링
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
            - 애플리케이션이 요청을 처리할 준비가 되는데 걸리는 시간
            - ApplicationReadyEvent로 측정
    - Spring MVC Metric
        - "http.server.requests"의 이름을 가지며 스프링 MVC 컨트롤러가 처리하는 모든 요청을 다룬다.
        - Tag를 사용해서 URI, method, status, exception, outcome 정보들을 분류해 확인할 수 있다.
    - DataSource Metric
        - "jdbc.connections"로 시작
        - 데이터소스와 커넥션 풀에 관한 Metric을 확인할 수 있으며 최대 커넥션, 최소 커넥션, 활성 커넥션, 대기 커넥션 수 등을 확인할 수 있다.
        - "hikaricp."를 통해 hikari 커넥션 풀에 대한 자세한 Metric을 확인할 수 있다.
    - 로그 Metric
        - logback.event:logback 로그에 대한 Metric을 확인할 수 있으며 trace, debug, info, warn, error 각각의 로그 레벨에 따른 로그 수 또한 확인이 가능하다.
    - tomcat Metric
        - "tomcat."으로 시작하며 아래의 옵션을 킬 경우 모든 tomcat Metric 사용이 가능하다.
            - ```yaml
          server:
          tomcat:
          mbeanregistry:
          enabled: true
          ```
        - 옵션을 키지 않을 경우 tomcat.session. 정보만 노출된다
        - tomcat의 최대 스레드, 사용 스레드 수 등을 확인할 수 있다.
    - 그 외에 HTTP 클라이언트 Metric, Cache Metric, 작업 실행 및 스케줄 Metric, spring 데이터 레포지토리 Metric, 몽고DB Metric, Redis Metric 등이 있다.
    - 다양한 Metric 들은 아래의 링크에서 확인이 가능하다.
        - [공식메뉴얼](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics.supported)