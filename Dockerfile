# --- Stage 1: Build the application ---
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy gradle executable and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Give execution permission to the gradle wrapper
RUN chmod +x gradlew

# Copy the source code
COPY src src

# Build the application (skipping tests for faster deployment)
RUN ./gradlew clean build -x test

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy only the built JAR file from the build stage
# This keeps the final image very small (no source code or JDK)
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port Spring Boot uses
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
