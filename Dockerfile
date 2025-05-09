FROM amazoncorretto:21

LABEL authors="Raul Esteban" version="dev-1" description="Spring Boot Docker Image"

# create and/or set the working firectory for subsequent commands
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


# Copy the project source code
COPY src src

# Install dependencies and build the project
# added -Pprod for production.
# also is possible that skip thest is nont longer necessary since db is now a render separate instance
RUN ./gradlew clean build -x test -Pprod

# lac-manufacture-v1.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application with production profile
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/build/libs/lac-manufacture-v1.jar"]
