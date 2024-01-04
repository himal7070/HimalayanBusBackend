# Use an OpenJDK base image suitable for running Java applications
FROM openjdk:17-jdk-alpine as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled jar file into the container (Assuming it's in the same directory as the Dockerfile)
COPY build/libs/HimalayanBus-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that the application runs on
EXPOSE 8080

# Define the command to run your application
CMD ["java", "-jar", "/app/app.jar"]
