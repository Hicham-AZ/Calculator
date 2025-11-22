##############################
# STAGE 1: Build with Maven
##############################
FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build using Maven (fat jar produced by shade plugin)
RUN mvn clean package -DskipTests


##############################
# STAGE 2: Run Application
##############################
FROM eclipse-temurin:11-jdk

WORKDIR /app

# Install required GUI libraries for Swing
RUN apt-get update && apt-get install -y \
    libxext6 libxrender1 libxtst6 libxi6 && \
    rm -rf /var/lib/apt/lists/*

# Copy the shaded JAR using exact jar name
COPY --from=builder /app/target/Calculator-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]

