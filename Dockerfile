FROM amazoncorretto:21
LABEL authors="raul esteban" version="dev-1" description="Spring Boot Docker Image"

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
#ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} lac-manufacture-v1.jar

# Expose the port the app runs on
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/lac-manufacture-v1.jar"]