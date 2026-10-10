
# syntax=docker/dockerfile:1

# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy build configuration first to maximize layer caching
COPY gradlew gradle.properties build.gradle settings.gradle ./
COPY gradle/ gradle/

RUN chmod +x gradlew

# Download and cache Gradle dependencies
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon

# Copy application sources after dependency resolution
COPY src/ src/

# Build the executable Spring Boot JAR
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon -x test \
    && test -n "$(find build/libs -maxdepth 1 -name '*.jar' -print -quit)"

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Copy only the executable Spring Boot JAR.
# Adjust the pattern if your project produces multiple JARs.
COPY --from=build /app/build/libs/*-boot.jar app.jar

# Create a dedicated non-root user
RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring \
    && chown spring:spring /app

USER spring:spring

# JVM container-aware memory settings
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
