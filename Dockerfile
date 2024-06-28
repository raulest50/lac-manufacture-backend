FROM gradle:8.1.1-jdk17-alpine AS build

LABEL authors="raul esteban" version="dev-1" description="Spring Boot Docker Image"

WORKDIR /app

# Verify the build output
RUN echo "test echo  -- -- - - - LAC MANUFACTURE APP"
RUN ls -la

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

RUN ls -la

# Ensure gradlew has execute permissions
RUN chmod +x gradlew
RUN ls -la

# Copy the project source code
COPY src src
RUN ls -la

# Install dependencies and build the project
RUN ./gradlew clean build -x test
RUN ls -la
RUN echo "build folder:"
RUN ls ./build -la
RUN ls ./build/libs -la


FROM amazoncorretto:21
RUN echo "AMAZON CORETTO"
RUN ls -la
RUN ls ./build -la
RUN ls ./build/libs -la

#ARG JAR_FILE=./build/libs/lac-manufacture-v1.jar
#COPY ${JAR_FILE} lac-manufacture-v1.jar
COPY --from=build /build/libs/lac-manufacture-v1.jar lac-manufacture-v1.jar

# Expose the port the app runs on
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/lac-manufacture-v1.jar"]

