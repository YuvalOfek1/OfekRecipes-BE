# Use an official Eclipse Temurin 21 JDK runtime as a parent image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to the container
COPY mvnw* pom.xml /app/
COPY .mvn /app/.mvn

# Ensure the Maven wrapper script is executable
RUN chmod +x mvnw

# Copy the source code to the container
COPY src /app/src

# Copy the uploads directory to the container
COPY uploads /app/uploads

# Copy the application configuration
COPY src/main/resources/application.yaml /app/application.yaml

# Install dependencies and package the application
RUN ./mvnw package -DskipTests

# Expose port 6969
EXPOSE 6969

# Run the application
CMD ["java", "-jar", "target/ofek_recipes-0.0.1-SNAPSHOT.jar"]