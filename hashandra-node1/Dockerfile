# Use lightweight JDK
FROM eclipse-temurin:25-jdk-jammy

# App directory
WORKDIR /app

# Copy jar
COPY build/libs/hashandra-node-1.0.jar app.jar

# Expose port (dynamic at runtime)
EXPOSE 8080

# Run app
ENTRYPOINT ["java","-jar","/app/app.jar"]