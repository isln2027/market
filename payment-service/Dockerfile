FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

COPY market-app/pom.xml ./market-app/pom.xml
COPY payment-api/pom.xml ./payment-api/pom.xml
COPY payment-service/pom.xml ./payment-service/pom.xml

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B -pl payment-service -am

COPY payment-service/src ./payment-service/src
COPY payment-api/src ./payment-api/src
RUN ./mvnw -B clean package -DskipTests -pl payment-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]