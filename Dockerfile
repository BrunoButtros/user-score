FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
ARG MODULE
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -ntp -pl "${MODULE}" -am package -DskipTests
RUN cp /src/${MODULE}/target/${MODULE}.*ar /src/application.war

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir /app/data && chown -R 10001:10001 /app
ARG MODULE
COPY --from=build --chown=10001:10001 /src/application.war /app/application.war
USER 10001:10001
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/application.war"]
