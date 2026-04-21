# Etapa 1: Construir el proyecto usando tu Maven Wrapper
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
# Le damos permisos de ejecución a tu archivo mvnw
RUN chmod +x ./mvnw
# Compilamos el proyecto omitiendo los tests para que sea más rápido
RUN ./mvnw clean package -Dmaven.test.skip=true
# Etapa 2: Levantar el servidor con tu web
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copiamos el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
# Exponemos el puerto estándar de Spring Boot
EXPOSE 8080
# Comando para iniciar tu web
ENTRYPOINT ["java", "-jar", "app.jar"]