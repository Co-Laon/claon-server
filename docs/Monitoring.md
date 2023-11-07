## Monitoring
***
### 1. Acutatior 개요
- Spring Boot Actuator는 애플리케이션의 상태를 모니터링하고 관리 기능을 제공하는 컴포넌트이다.
- 이를 사용하여 Bean, Mapping, Configuration 등의 정보들을 조회할 수 있으며 로그 수준을 동적으로 변경할 수 있다.

### 2. Usage Setting
- 의존성 추가
```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```
- Actuator Endpoint 노출 설정
  - 모든 Endpoint 노출
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: *
  ```
  - 특정 Endpoint만 노출
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: {Endpoint name}
  ```

### 3. Endpoint 사용 방법
- Actuator Endpoint를 통해 Spring Boot 애플리케이션을 모니터링하고 상호작용을 할 수 있다.
  - 애플리케이션을 실행한 후 웹 브라우저나 REST Client를 사용하여 해당 엔드포인트에 접근이 가능
  - Spring Boot에는 기본 제공 Endpoint가 포함되어 있으며 사용자 지정 Endpoint 추가도 가능
  - 사용가능한 Endpoint 목록은 아래와 같다.
  ![Endpoint List](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdDOzZZ%2Fbtrpjw1Zfqv%2F2i2oveiPRiB8Pfa6X7ocW1%2Fimg.png)
  - 웹 애플리케이션(Spring MVC, Spring WebFlux, Jersey)을 사용한다면 아래의 Endpoint를 추가로 사용할 수 있다.
  ![Additional_Endpoint_List](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fq5vzQ%2FbtrpcQgjyYB%2FsQp612OvtDmcBzRKzKNW10%2Fimg.png)
- Actuator Endpoint의 기본 prefix는 "/actuator"이며 이를 아래와 같이 변경할 수 있다.
  ```yaml
  management:
    endpoints:
      web:
        base-path: /{custom prefix}
  ```
    - 이를 통해 /{custom prefix}/{endpoint name}과 같이 사용이 가능
- 사용자 정의 Endpoint를 추가할 수 있다.
  - 사용자 정의 Endpoint는 특정한 비즈니스 로직에 대한 모니터링 정보 제공이 가능
  - 사용하는 어노테이션 목록
    - @Endpoint(id = {id}) : 지정된 {id}에 사용자 정의 Endpoint를 생성
    - @ReadOperation : 해당 Endpoint에서 수행할 동작을 정의
  - 예시 코드
  ```java
  @Component
  @Endpoint(id = "customEndpoint")
  public class CustomEndpoint {
  
    @ReadOperation
    public CustomInfo customInfo() {
        return new CustomInfo("This is a custom endpoint", "v1.0");
    }
  
    public static class CustomInfo {
        private final String description;
        private final String version;
  
        public CustomInfo(String description, String version) {
            this.description = description;
            this.version = version;
        }
  
        public String getDescription() {
            return description;
        }
  
        public String getVersion() {
            return version;
        }
    }
  }
  ```
  - 아래의 URL을 통해 위의 예시 코드에서 정의한 사용자 정의 Endpoint 확인이 가능
  ```http://localhost:8080/actuator/customEndpoint```

### 4. 보안 및 인증
- Actuator Endpoint는 민감한 정보를 포함할 수 있으므로 Spring Security를 사용하여 Actuator Endpoint에 대한 인증을 구현할 수 있다.
- 의존성 추가
  ```
  dependencies {
      implementation 'org.springframework.boot:spring-boot-starter-security'
  }
  ```
- 구현 예시 코드
  ```java
    @Configuration
      public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
          .authorizeRequests()
          .antMatchers("/actuator/**").authenticated()
          .anyRequest().permitAll()
          .and()
          .httpBasic();
      }
    }
  ```
  - '/actuator/**' Endpoint에 대해 기본 인증이 필요하도록 설정
  - Actuator Endpoint에 접근하기 위해서는 사용자 이름과 비밀번호가 필요

- 사용자 이름 및 비밀번호 설정
  - 기본적으로 Spring Boot Security는 사용자 이름은 'user' 및 자동 생성된 비밀번호를 사용하며 해당 비밀번호는 애플리케이션 구동시 콘솔에 출력됨
  - 사용자 이름 및 비밀번호은 아래와 같이 변경이 가능
  ```yaml
  spring:
    security:
      user:
        name: {Username}
        password: {Password}
  ```
- Endpoint에 대한 권한 설정
  - 특정 Endpoint에 대한 RBAC(역할기반 접근제어)는 Spring Security 설정 클래스에서 권한 설정 추가를 통해 구현이 가능함.
  - 사용자 정의 Endpoint를 추가하고 권한 설정을 통해 Endpoint에 대한 접근 제어가 가능
- 권한 설정 예시 코드
  ```java
  @Configuration
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            .and()
            .httpBasic();
    }
  }
  ```
    - 'actuator/health' Endpoint에 대해 모든 사용자가 접근이 가능하도록 허용하고 그 외의 Actuator Endpoint는 'ADMIN' 역할을 가진 사용자만 접근이 가능하도록 설정
***
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

### 4. Micrometer Metrics
- Metrics Endpoint를 사용하면 기본으로 제공되는 Metric들을 확인할 수 있다.
  - `http://localhost:8080/actuator/metrics/{name}`
- Tag를 기반으로 정보를 필티렁할 수 있는 Tag 필터 기능을 제공한다.
  - Tag는 KEY:VALUE와 같은 형식을 사용해야 한다.
  - 힙 메모리 확인
    - `http://localhost:8080/actuator/metrics/jvm.memory.used?tag=area:heap`
  - 힙이 아닌 메모리 확인
    - `http://localhost:8080/actuator/metrics/jvm.memory.used?tag=area:nonheap`
  - log 요청과 HTTP Status 200 필터링
    - `http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/log&tag=status:200`
- JVM Metric
  - "jvm."으로 시작
  - 메모리와 버퍼 풀 세부 정보, 가비지 수집 관련 통계, 스레드 활용, 로드 및 언로드된 클래스수, JVM 버전 정보, JIT 컴파일 시간 등을 확인 가능
- 시스템 Metric
  - "system.", "process.", "disk."으로 시작
  - CPU 지표, 파일 디스크립터, 가동 시간, 사용 가능한 디스크 공간 등을 확인 가능
- 애플리케이션 시작 Metric
  - application.started.time: 애플리케이션을 시작하는데 걸리는 시간 (ApplicationStartedEvent로 측정)
  - application.ready.time: 애플리케이션이 요청을 처리할 준비가 되는데 걸리는 시간 (ApplicationReadyEvent로 측정)
  - ApplicationStartedEvent: 스프링 컨테이너가 완전히 실행된 상태이며 이후에 커맨드 라인 러너가 호출
  - ApplicationReadyEvent: 커맨드 라인 러너가 실행된 이후에 호출
- 스프링 MVC Metric
  - "http.server.requests"의 이름을 가지며 스프링 MVC 컨트롤러가 처리하는 모든 요청을 다룬다.
  - Tag를 사용해서 URI, method, status, exception, outcome 정보들을 분류해 확인할 수 있다.
- 데이터소스 Metric
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