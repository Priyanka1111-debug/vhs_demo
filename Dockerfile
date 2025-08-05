FROM openjdk:17-jdk-slim
COPY build/libs/my-java-app.jar /app/my-java-app.jar
WORKDIR /app
CMD ["java", "-jar", "my-java-app.jar"]