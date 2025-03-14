FROM openjdk:21
WORKDIR /app
COPY src/main/resources/Interns_2025_SWIFT_CODES.csv /app/src/main/resources/Interns_2025_SWIFT_CODES.csv
COPY target/intern-task-1.0.0-SNAPSHOT.jar /app/intern-task-1.0.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "intern-task-1.0.0-SNAPSHOT.jar"]
