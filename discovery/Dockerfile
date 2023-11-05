FROM openjdk:17

COPY build/libs/discovery-*.jar /usr/local/bin/discovery.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/discovery.jar"]