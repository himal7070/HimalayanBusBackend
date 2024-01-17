## OpenJDK base image suitable for running Java applications
#FROM openjdk:17-jdk-alpine as builder
#
## working directory inside the container
#WORKDIR /app
#
## compiled jar file into the container
#COPY build/libs/HimalayanBus-0.0.1-SNAPSHOT.jar /app/app.jar
#
##port that the application runs on
#EXPOSE 8080
#
##command to run your application
#CMD ["java", "-jar", "/app/app.jar"]



# Use the OpenJDK base image suitable for running Java applications
FROM openjdk:17-jdk-alpine as builder

# Working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY build/libs/HimalayanBus-0.0.1-SNAPSHOT.jar /app/app.jar

# Install bash
RUN apk --no-cache add bash

# Expose the port that the application runs on
EXPOSE 8080

# Command to run your application
CMD ["bash", "-c", "java -jar /app/app.jar"]
