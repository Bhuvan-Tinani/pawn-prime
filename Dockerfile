# 1. Use Java 17 (Matches your Spring Boot needs)
FROM eclipse-temurin:17-jdk-alpine

# 2. Set the working directory to /app
#    This means all future commands happen inside /app in the container
WORKDIR /app

# 3. Copy the built JAR file from your 'target' folder into the container
#    (Make sure to run 'mvn package' on your PC first!)
COPY target/*.jar app.jar

# 4. Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]