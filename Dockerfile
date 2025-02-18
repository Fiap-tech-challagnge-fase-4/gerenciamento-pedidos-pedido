FROM maven:3.9.8-amazoncorretto-17-al2023 AS build

WORKDIR /app

COPY . /app

RUN mvn clean package

FROM openjdk:17-ea-17-jdk-slim-buster

WORKDIR /app

COPY --from=build /app/target/*.jar pedidoApp.jar

EXPOSE 8084

CMD ["java", "-jar", "pedidoApp.jar"]
