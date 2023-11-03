FROM openjdk:17

COPY build/libs/auth-*.jar /usr/local/bin/auth.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/auth.jar"]