FROM openjdk:17

COPY build/libs/monitor-*.jar /usr/local/bin/monitor.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/monitor.jar"]