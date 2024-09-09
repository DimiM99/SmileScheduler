
#
# Chace Stage (gets the dependencies and caches them, subsequent builds will use the cache)
#
FROM gradle:8.10.0-jdk21 AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/java-code/
WORKDIR /home/gradle/java-code
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

#
# Build stage (builds the application)
#
FROM gradle:8.10.0-jdk21 AS builder

ARG JWT_SECRET_KEY

ENV JWT_SECRET_KEY=secret

ENV DOCKER_HOST=tcp://host.docker.internal:2375
ENV TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal

COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/java-code/
WORKDIR /usr/src/java-code
RUN gradle test --no-daemon
RUN gradle bootJar -i --stacktrace

#
# Package run stage
#
FROM openjdk:21-slim
ENV TZ="Europe/Berlin"
USER root
WORKDIR /usr/src/java-app
COPY --from=builder /usr/src/java-code/build/libs/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
