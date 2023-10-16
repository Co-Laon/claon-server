## Spring Cloud Eureka

### 1. 개요

- Eureka는 클라우드 환경의 다수의 서비스(예: API 서버)들의 로드 밸런싱* 및 장애 조치 목적을 가진 미들웨어서버**이다.
    - 로드 밸런싱 : 특정 서비스를 제공하는 서버가 여러대가 있을 때 트래픽을 한 서버에 몰리지 않게 분산해주는 기술이다.
    - 미들웨어 : 데이터를 주고 받는 양쪽의 서비스(웹의 예로 클라이언트와 API 서버)의 중간에 위치해 매개 역할을 하는 소프트웨어다.
- Eureka는 이러한 미들웨어 기능을 하기 위해 각 연결된 서비스의 IP / PORT /InstanceId를 가지고 있고 REST기반으로 작동한다
- Eureka는 Client-Sever 방식으로 Eureka Server에 등록된 서비스는 Eureka Client로 불린다.

### 2. 구성
![Eureka 프로세스](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FlL32n%2FbtreRSuWGXV%2FuIQYsqZ4hNxKVzI542aBCK%2Fimg.png)
- 각 서비스를 Eureka Server에 등록하게 되면 Eureka Server는 각 Eureka Client의 IP / PORT / InstanceId를 저장한다.
- 이후 Eureka Client가 다른 Eureka Client에게 요청을 보낼 때 Eureka에서 받아온 정보를 가지고 요청을 보낼 수 있다.
- 각 개체별 역할
  - 서비스가 Eureka Server에 등록될 때 자신이 살아있다는 상태값을 보낸다.
  - Eureka Server는 다른 Eureka Client의 정보들을 제공하고 서비스는 Local Cache에 저장한다.
  - 이후 30초(Default)마다 Eureka Server에 Heartbeats 요청을 보내고 Eureka Server는 90초 안에 Headerbeats가 도착하지 않으면 해당 Eureka Client를 제거한다.

### 3. REST API 
| **Operation**    | **HTTP Action**                        | **Description**                                               |
|------------------|----------------------------------------|---------------------------------------------------------------|
| Eureka Client 등록 | Post /eureka/apps/{appID}              | Input: JSON/XML payload HTTP Code: 204 on success             |
| Eureka Client 삭제 | DELETE /eureka/apps/appID/{instanceID} | HTTP Code: 200 on success                                     |
| Heartbeats       | PUT /eureka/apps/appID/{instanceID}    | HTTP Code: * 200 on success * 404 if instanceID doesn’t exist |
| Eureka Client 목록 | GET /eureka/apps                       | HTTP Code: 200 on success Output: JSON/XML                    |

### 4. Eureka Server Setting
- Gradle 의존성 추가
```
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
}
```
- 메인 클래스에 @EnableEurekaServer 어노테이션을 붙여 Eureka Server임을 알려준다.
```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}
```

- 해당 application에 대한 Port 설정
```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-service

eureka:
  client:
    register-with-eureka: false #eureka server를 registry에 등록할지 여부
    fetch-registry: false       #registry에 있는 정보들을 가져올지 여부
```

### 5. Eureka Client Setting
- Gradle 의존성 추가
```
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
}
```
- 메인 클래스에 @EnableEurekaClient 어노테이션을 붙여 Eureka Client임을 알려준다.
``` java
@SpringBootApplication
@EnableDiscoveryClient
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
- 해당 application에 대한 Port 설정
``` yaml
spring:
  application:
    name: eureka-client-ex

server:
  port: 8080

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka #Eureka Server 명시
```

### 6. Eureka Dashboard 출력
- http://localhost:8761
