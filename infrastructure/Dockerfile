# syntax=docker/dockerfile:1.4
# Multi-stage Dockerfile for all Spring Boot services
# Usage: docker build -f infrastructure/Dockerfile --build-arg SERVICE_NAME=auth-service -t leetcode/auth-service .

ARG SERVICE_NAME
ARG SERVICE_PATH=services

# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

ENV MAVEN_CLI_OPTS="--batch-mode --no-transfer-progress -Dmaven.test.skip=true -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.retryHandler.requestSentEnabled=true -Dmaven.wagon.http.pool=false -Daether.connector.http.connectionMaxTtl=25 -Daether.connector.http.requestTimeout=120000 -Daether.connector.http.connectionTimeout=30000 -Dhttp.keepAlive=false"

# Copy parent pom first for better caching
COPY pom.xml .

# Copy all service poms
COPY infrastructure/ infrastructure/
COPY services/ services/
COPY infrastructure/maven-settings.xml /usr/share/maven/ref/settings-docker.xml

# Build specific service
ARG SERVICE_PATH
ARG SERVICE_NAME
RUN --mount=type=cache,target=/root/.m2 if [ "$SERVICE_PATH" = "infrastructure" ]; then \
        cd infrastructure/${SERVICE_NAME} && mvn -s /usr/share/maven/ref/settings-docker.xml $MAVEN_CLI_OPTS clean package; \
    else \
        cd services/${SERVICE_NAME} && mvn -s /usr/share/maven/ref/settings-docker.xml $MAVEN_CLI_OPTS clean package; \
    fi

# Stage 2: Runtime with JRE
FROM eclipse-temurin:21-jre-alpine

ARG INSTALL_DOCKER_CLI=false

RUN if [ "$INSTALL_DOCKER_CLI" = "true" ]; then \
        apk add --no-cache docker-cli; \
    fi

WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /app /judge-temp /tmp/judge-service \
    && chown -R spring:spring /app /judge-temp /tmp/judge-service
USER spring:spring

# Copy built jar from builder stage
ARG SERVICE_PATH
ARG SERVICE_NAME
COPY --from=builder /build/${SERVICE_PATH}/${SERVICE_NAME}/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose default port (can be overridden)
EXPOSE 8080

# JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

