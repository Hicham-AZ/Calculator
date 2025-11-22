############ BUILD STAGE ############
# JDK with Maven built-in, supports Swing
FROM maven:3.9.11-eclipse-temurin-17-alpine AS builder

# Swing works because JDK (not JRE-headless) is included
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

# Build the Jar
RUN mvn clean package -DskipTests


############ RUNTIME STAGE ############
# Use a full JDK (not headless) so Swing UI runs
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only the built jar
COPY --from=builder /workspace/target/*.jar App.jar

# Swing needs to access display (X11 on Linux)
# If using Windows/Mac Docker Desktop, it opens automatically.
CMD ["java", "-jar", "App.jar"]

