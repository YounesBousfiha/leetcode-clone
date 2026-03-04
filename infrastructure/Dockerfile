# Multi-stage Dockerfile for all Spring Boot services
# Usage: docker build -f infrastructure/Dockerfile --build-arg SERVICE_NAME=auth-service -t leetcode/auth-service .

ARG SERVICE_NAME
ARG SERVICE_PATH=services

# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy parent pom first for better caching
COPY pom.xml .

# Copy all service poms
COPY infrastructure/ infrastructure/
COPY services/ services/

# Build specific service
ARG SERVICE_PATH
ARG SERVICE_NAME
RUN if [ "$SERVICE_PATH" = "infrastructure" ]; then \
        cd infrastructure/${SERVICE_NAME} && mvn clean package -DskipTests; \
    else \
        cd services/${SERVICE_NAME} && mvn clean package -DskipTests; \
    fi

# Stage 2: Runtime with JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
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

