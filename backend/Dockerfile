# syntax=docker/dockerfile:1
# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp -q dependency:go-offline
COPY src src
RUN ./mvnw -B -ntp -q package -DskipTests -Djacoco.skip=true \
    && cp target/examforge-backend-*.jar /workspace/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
RUN groupadd --system --gid 10001 examforge \
    && useradd --system --uid 10001 --gid examforge --no-create-home examforge
WORKDIR /app
COPY --from=build /workspace/app.jar app.jar
USER examforge
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
