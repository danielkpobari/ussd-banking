# Use official Java 17 image as base
FROM openjdk:17-jdk-alpine

# Set working directory to /app
WORKDIR /app

# Copy project files
COPY . .

# Expose port 8080 for the application
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
CMD ["java", "-jar", "target/ussd-application.jar"]