# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy wrapper & settings for optimal dependency layer caching
COPY gradlew gradle.properties build.gradle settings.gradle ./
COPY gradle gradle

RUN chmod +x gradlew

COPY src src

# Build executable JAR without running tests or spawning extra background daemons
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy single executable JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create non-root system user & assign permissions
RUN groupadd -r spring && useradd -r -g spring spring \
    && chown -R spring:spring /app

USER spring:spring

# Configure JVM container memory management & high-entropy random generation
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["java", "-jar", "app.jar"]