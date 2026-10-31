# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY spam-shield-common spam-shield-common
COPY call-records-mcp-server call-records-mcp-server
COPY call-records-rest-server call-records-rest-server
RUN chmod +x mvnw && ./mvnw -B -ntp package -DskipTests

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
RUN useradd --system --uid 10001 --home-dir /app appuser \
    && chown appuser:appuser /app
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

FROM runtime AS call-records-mcp-server
COPY --from=build --chown=appuser:appuser /src/call-records-mcp-server/target/call-records-mcp-server.jar /app/app.jar

FROM runtime AS call-records-rest-server
COPY --from=build --chown=appuser:appuser /src/call-records-rest-server/target/call-records-rest-server.jar /app/app.jar
