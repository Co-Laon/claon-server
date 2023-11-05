FROM openjdk:17

COPY build/libs/user-*.jar /usr/local/bin/user.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/user.jar"]