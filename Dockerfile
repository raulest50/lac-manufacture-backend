# Stage 1: Build the application
FROM gradle:8.1.1-jdk17-alpine AS build

LABEL authors="Raul Esteban" version="dev-1" description="Spring Boot Docker Image"

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

# Ensure gradlew has execute permissions
RUN chmod +x gradlew

# Copy the project source code
COPY src src

# Install dependencies and build the project
RUN ./gradlew clean build -x test

# Stage 2: Package the application
FROM amazoncorretto:21

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
