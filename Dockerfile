
FROM amazoncorretto:21

LABEL authors="raul esteban" version="dev-1" description="Spring Boot Docker Image"

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Copy the project source code
COPY src src

# Install dependencies and build the project
RUN ./gradlew clean build -x test

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} lac-manufacture-v1.jar

# Expose the port the app runs on
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/lac-manufacture-v1.jar"]

