## Spring Cloud Eureka

### 1. 개요
- Eureka는 클라우드 환경의 다수의 서비스(예: API 서버)들의 로드 밸런싱* 및 장애 조치 목적을 가진 미들웨어서버**이다.
    - 로드 밸런싱 : 특정 서비스를 제공하는 서버가 여러대가 있을 때 트래픽을 한 서버에 몰리지 않게 분산해주는 기술이다.
    - 미들웨어 : 데이터를 주고 받는 양쪽의 서비스(웹의 예로 클라이언트와 API 서버)의 중간에 위치해 매개 역할을 하는 소프트웨어다.
- Eureka는 이러한 미들웨어 기능을 하기 위해 각 연결된 서비스의 IP / PORT / InstanceId를 가지고 있고 REST기반으로 작동한다
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
| **Operation**                                                 | **HTTP Action**                                                                                                                                                           | **Description**                                                                       |
|---------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| Register new application Client                               | Post /eureka/v2/apps/{appID}                                                                                                                                              | Input: JSON/XML payload HTTP Code: 204 on success                                     |
| De-register application instance                              | DELETE /eureka/v2/apps/{appID}/{instanceID}                                                                                                                               | HTTP Code: 200 on success                                                             |
| Send application instance heartbeats                          | PUT /eureka/v2/apps/{appID}/{instanceID}                                                                                                                                  | HTTP Code: * 200 on success * 404 if instanceID doesn’t exist                         |
| Query for all instances                                       | GET /eureka/v2/apps                                                                                                                                                       | HTTP Code: 200 on success Output: JSON/XML                                            |
| Query for all appID instance                                  | Get /eureka/v2/apps/{appID}                                                                                                                                               | HTTP Code: 200 on success Output: JSON/XML                                            |
| Query for a specific appID/instanceID                         | GET /eureka/v2/apps/{appID}/{instanceID}                                                                                                                                  | HTTP Code: 200 on success Output: JSON/XML                                            |
| Query for a specific instanceID                               | GET /eureka/v2/instances/{instanceID}                                                                                                                                     | HTTP Code: 200 on success Output: JSON/XML                                            |
| Take instance out of service                                  | PUT /eureka/v2/apps/{appID}/{instanceID}/status?value=OUT_OF_SERVICE                                                                                                      | HTTP Code: * 200 on success * 500 on failure                                          |
| Move instance back into service (remove override)             | DELETE /eureka/v2/apps/{appID}/{instanceID}/status?value=UP (The value=UP is optional, it is used as a suggestion for the fallback status due to removal of the override) | HTTP Code: * 200 on success * 500 on failure                                          |
| Update metadata                                               | PUT /eureka/v2/apps/{appID}/{instanceID}/metadata?key=value                                                                                                               | HTTP Code:* 200 on success * 500 on failure                                           |
| Query for all instances under a particular vip address        | GET /eureka/v2/vips/vipAddress                                                                                                                                            | * HTTP Code: 200 on success Output: JSON/XML * 404 if the vipAddress does not exist.  |
| Query for all instances under a particular secure vip address | GET /eureka/v2/svips/svipAddres                                                                                                                                           | * HTTP Code: 200 on success Output: JSON/XML * 404 if the svipAddress does not exist. |

### 4. Eureka Server Setting
- Server Config
  - enable-self-preservation
    - client가 자신의 상태를 제 시간에 갱신하지 않는 수가 일정 수가 넘게 되면 등록 만료를 멈춤
    - 네트워크 장애가 발생 시 등록된 모든 서비스가 해제되는 것을 방지
    - 활성화 시 registry에서 해당 instance 를 정해진 기간동안 제거하지 않음
    - 활성화 여부는 Expected heartbeats 와 actual heartbeats 수를 비교하여 결정
    - default = true
  - eviction-interval-time-in-ms
    - client로 부터 heartbeat가 없으면 제거하는 시간
    - default = 60 * 1000ms (60s)
  - response-cache-update-interval-ms
    - Eureka REST API에서 응답을 캐시하는 시간
    - 해당 시간이 지나야만 REST API에서 client 등록 정보가 바뀐 것을 표시
    - default = 30 * 1000ms (30s)
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
  port: {port}

spring:
  application:
    name: discovery-service

eureka:
  client:
    register-with-eureka: false #eureka server를 registry에 등록할지 여부
    fetch-registry: false       #registry에 있는 정보들을 가져올지 여부
```

### 5. Eureka Client Setting
- Client Config
  - register-with-eureka
    - 유레카 등록 여부
    - default = true
  - fetch-registry
    - 유레카에서 조회하는지 여부
    - default = true
  - registry-fetch-interval-seconds
    - client 쪽에서 eureka registry를 캐싱하는 시간
    - default = 30s
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
  port: {port}

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:{port}/eureka #Eureka Server 명시
```

### 6. Instance Config
- lease-renewal-interval-in-seconds
  - Client에서 heartbeat를 Server에 보내는 시간
  - default = 30s
- lease-expiration-duration-in-seconds
  - 해당시간동안 Server에서 heartbeat가 수신되지 않으면 eureka 에서 해당 instance 제거
  - default = 90s
- prefer-ip-address
  - 유레카 등록 시 host name 대신 ip 주소 사용
  - 컨테이너 기반 배포에서 컨테이너는 DNS 엔트리가 없는 임의의 생성된 host name을 부여받아 시작하므로 해당 값이 false 인 경우에는 hostname 위치를 얻지 못함
    - 이 때, 한 서버에 2개 이상의 네트워크 인터페이스가 있는 경우 문제 발생
    - 이럴 때는 네트워크 인터페이스를 선택하거나 원하는 네트워크 주소를 정의
  - default = false

### 7. Eureka Dashboard 출력
- http://localhost:8761