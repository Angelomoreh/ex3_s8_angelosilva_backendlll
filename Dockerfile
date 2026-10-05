FROM maven:3.9.16-eclipse-temurin-17-alpine AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine AS config-server
WORKDIR /app
COPY --from=build /workspace/config-server/target/config-server-1.0.0.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS discovery-server
WORKDIR /app
COPY --from=build /workspace/discovery-server/target/discovery-server-1.0.0.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS api-gateway
WORKDIR /app
COPY --from=build /workspace/api-gateway/target/api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS cuentas-service
WORKDIR /app
COPY --from=build /workspace/cuentas-service/target/cuentas-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS transacciones-service
WORKDIR /app
COPY --from=build /workspace/transacciones-service/target/transacciones-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS bff-web
WORKDIR /app
COPY --from=build /workspace/bff-web/target/bff-web-1.0.0.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS bff-mobile
WORKDIR /app
COPY --from=build /workspace/bff-mobile/target/bff-mobile-1.0.0.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jre-alpine AS bff-atm
WORKDIR /app
COPY --from=build /workspace/bff-atm/target/bff-atm-1.0.0.jar app.jar
EXPOSE 8093
ENTRYPOINT ["java", "-jar", "app.jar"]
