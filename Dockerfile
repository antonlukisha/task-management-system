FROM openjdk:17-jdk-slim AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml ./
COPY src ./src

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/TaskManagementSystem-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
