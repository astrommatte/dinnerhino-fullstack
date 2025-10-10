# ---- Bygg fas ----
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---- Kör fas ----
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render tilldelar en port via env-variabeln PORT – Spring Boot måste använda den
ENV PORT=8080
EXPOSE 8080

# Kör appen
ENTRYPOINT ["java", "-jar", "app.jar"]