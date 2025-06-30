# Etapa de construcción (con JDK y Maven)
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de ejecución ligera (solo JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el JAR desde la etapa de construcción
COPY --from=builder /app/target/App-0.0.1-SNAPSHOT.jar app.jar

# Copia el script modificado (sin la parte de compilación)
COPY run.sh /run.sh
RUN chmod +x /run.sh

EXPOSE 4000
ENTRYPOINT ["/run.sh"]
