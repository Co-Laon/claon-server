FROM openjdk:17

COPY build/libs/post-*.jar /usr/local/bin/post.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/post.jar"]