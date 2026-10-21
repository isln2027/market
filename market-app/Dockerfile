FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

COPY market-app/pom.xml ./market-app/pom.xml
COPY payment-api/pom.xml ./payment-api/pom.xml
COPY payment-service/pom.xml ./payment-service/pom.xml

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B -pl market-app -am

COPY market-app/src ./market-app/src
RUN ./mvnw -B clean package -DskipTests -pl market-app -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/market-app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]