FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

COPY src ./src

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar target/market-*.jar"]