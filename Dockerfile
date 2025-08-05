# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from your host machine into the container
COPY build/libs/*.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
