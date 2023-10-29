## Monitoring
### 1. 개요
- Spring Boot Actuator는 애플리케이의 상태를 모니터링하고 관리 기능을 제공하는 컴포넌트이다.
- 이를 사용하여 Bean, Mapping, Configuration 등의 정보들을 조회할 수 있으며 로그 수준 동적으로 변경할 수 있다.

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
- Actuator Endpoint의 기본 prefix는 /actuator이며 이를 아래와 같이 변경할 수 있다.
  ```yaml
  management:
    endpoints:
      web:
        base-path: /{custom prefix}
  ```
    - 이를 통해 /{custom prefix}/{endpoint name}과 같이 사용이 가능
- 사용자 정의 Endpoint 추가
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
  - 위 예시 코드에서 아래의 URL에 접근하면 사용자 정의 Endpoint 확인이 가능
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