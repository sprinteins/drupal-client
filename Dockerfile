# Use the official Maven image as the base image
FROM maven:3.8.3-openjdk-11 as build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code to the working directory
COPY pom.xml ./
COPY src ./src/

# Build the project
RUN mvn clean package &&  \
  find /app/target -name 'drupal-client-*-jar-with-dependencies.jar' -exec cp {} /app/drupal-client.jar \;

# Use the official OpenJDK image as the base image for the runtime environment
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the generated JAR file from the build stage
COPY --from=build app/drupal-client.jar /app/drupal-client.jar

# Set the entrypoint for the container
ENTRYPOINT ["java", "-jar", "/app/drupal-client.jar"]

# Pass arguments to the CLI tool when running the container
CMD ["--help"]
