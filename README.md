# CLAON

[![Java 17.0.8](https://img.shields.io/badge/java-17.0.8-blue")](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
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

## Index
- [Spring Cloud Eureka](./docs/SpringCloudEureka.md)
- [SpringBoot Admin](./docs/SpringBootAdmin.md)
- [Spring Cloud Gateway](./docs/SpringCloudGateway.md)
- [Spring Cloud OpenFeign](./docs/SpringCloudOpenFeign.md)
- [Monitoring](./docs/Monitoring.md)
- [Resiliency Pattern](./docs/ResiliencyPattern.md)
