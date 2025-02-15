FROM maven:3.9.8-amazoncorretto-17-al2023 AS build

WORKDIR /app

COPY . /app

RUN mvn clean package

FROM openjdk:17-ea-17-jdk-slim-buster

WORKDIR /app

COPY --from=build /app/target/*.jar clienteApp.jar

EXPOSE 8081

CMD ["java", "-jar", "clienteApp.jar"]
