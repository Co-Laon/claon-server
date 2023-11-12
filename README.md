# CLAON

[![Jdk 17.0.8](https://img.shields.io/badge/jdk-17.0.8-blue")](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![SpringBoot 3.1.2](https://img.shields.io/badge/spring_boot-3.1.2-blue")](https://spring.io/projects/spring-boot)
[![CI](https://github.com/Co-Laon/claon-server/actions/workflows/ci.yml/badge.svg)](https://github.com/Co-Laon/claon-server/actions/workflows/ci.yml)

## Build

1. Build all projects with gradle
```bash
./gradlew cleanAll buildAll
```

2. Create docker images
```bash
docker-compose -f docker-compose.yml up -d
```

## Services
- [discovery](./docs/discovery.md)
  - [eureka](./docs/eureka.md)
  - [actuator](./docs/actuator.md)
- [monitor](./docs/monitor.md)
  - [spring_boot_admin](./docs/spring_boot_admin.md)
  - [micrometer](./docs/micrometer.md)
  - [zipkin](./docs/zipkin.md)
- [gateway](./docs/gateway.md)
  - [spring cloud gateway](./docs/spring_cloud_gateway.md)
  - [resilience4j](./docs/resilience4j.md)
- [auth, user, center, post](./docs/service.md)
  - [openfeign](./docs/openfeign.md)
