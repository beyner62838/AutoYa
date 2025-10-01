# backend/Dockerfile

# Etapa build
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar descriptor de dependencias
COPY pom.xml ./

# Descargar dependencias para cachear
RUN mvn -B dependency:go-offline

# Copiar el resto del código
COPY src ./src

# Construir el JAR
RUN mvn -B clean package -DskipTests

# Etapa runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]