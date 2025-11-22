##############################
# STAGE 1: Build with Maven
##############################
FROM maven:3.9.6-eclipse-temurin-11 AS build

# Set working directory
WORKDIR /app

# Copy source code
COPY pom.xml .
COPY src ./src

# Build fat JAR using Maven Shade plugin
RUN mvn clean package -DskipTests


##############################
# STAGE 2: Run the application
##############################
FROM eclipse-temurin:11-jdk

WORKDIR /app

# Install required GUI libraries for Java Swing inside Docker
RUN apt-get update && apt-get install -y \
    libxext6 libxrender1 libxtst6 libxi6 && \
    rm -rf /var/lib/apt/lists/*

# Copy the fat JAR from the build stage
COPY --from=build /app/target/Calculator-1.0-SNAPSHOT.jar app.jar

# Default command to run your Java GUI application
CMD ["java", "-jar", "app.jar"]

