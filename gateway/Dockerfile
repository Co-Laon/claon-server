FROM openjdk:17

COPY build/libs/gateway-*.jar /usr/local/bin/gateway.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/gateway.jar"]