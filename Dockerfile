# Use a base image with Java 17 installed
FROM openjdk:17-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/dubaipolica-0.0.1-dubaipolica.jar /app/

# Expose the port your application runs on
EXPOSE 8082

# Command to run the application
CMD ["java", "-jar", "dubaipolica-0.0.1-dubaipolica.jar"]