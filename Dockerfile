FROM openjdk:21-jdk-slim AS greadle-download

ENV GRADLE_USER_HOME="/home/.gradle"

WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew

RUN ./gradlew --version

COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

RUN ./gradlew tasks

FROM openjdk:21-jdk-slim AS linux-builder

RUN apt-get update && apt-get install -y binutils fakeroot

ENV GRADLE_USER_HOME="/home/.gradle"

COPY --from=greadle-download /home/.gradle /home/.gradle
COPY --from=greadle-download /app /app

WORKDIR /app

COPY src src

RUN ./gradlew jpackage