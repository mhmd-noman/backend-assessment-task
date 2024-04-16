# Build stage
FROM gradle:jdk21 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
# Only copy necessary files to take advantage of Docker's layer caching
COPY src ./src
RUN gradle build --no-daemon

# Staging stage
FROM openjdk:21-jdk-slim AS staging
WORKDIR /app
COPY --from=build /app/build/libs/app-*.jar ./app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

# Production stage
FROM openjdk:21-jdk-slim AS production
WORKDIR /app
COPY --from=staging /app/app.jar ./app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
