#
# Build stage
#
FROM gradle:7.5-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build -x test

#
# Package stage
#
FROM amazoncorretto:21
COPY --from=build /home/gradle/src/build/libs/*.jar /app/lac-manufacture-v1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/lac-manufacture-v1.jar"]
