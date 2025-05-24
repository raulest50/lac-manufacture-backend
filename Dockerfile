# Build stage
FROM amazoncorretto:21 AS builder

LABEL authors="Raul Esteban" version="dev-1" description="Spring Boot Docker Image"

# Create and set the working directory for build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

# Ensure gradlew has execute permissions
RUN chmod +x gradlew

# Fix the line endings in the gradlew script
RUN sed -i 's/\r$//' gradlew

# Cache dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the project source code
COPY src src

# Install dependencies and build the project
# added -Pprod for production.
RUN ./gradlew clean build -x test -Pprod --no-daemon

# Runtime stage
FROM amazoncorretto:21

# Create and set the working directory for runtime
WORKDIR /app

# Create a non-root user and group
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy only the built JAR from the builder stage
COPY --from=builder /app/build/libs/lac-manufacture-v1.jar app.jar

# Set ownership of the application files to the non-root user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application with production profile
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
