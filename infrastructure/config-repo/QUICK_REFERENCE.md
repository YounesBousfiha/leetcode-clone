# Quick Reference - Service Configuration

## Service Endpoints

### Development Environment
```
API Gateway:      http://localhost:8080
Auth Service:     http://localhost:8081
User Service:     http://localhost:8082
Judge Service:    http://localhost:8083
AI Assist:        http://localhost:8084
Metrics Service:  http://localhost:8085
Problem Service:  http://localhost:8086
Config Server:    http://localhost:8888
Eureka Server:    http://localhost:8761
```

### Kubernetes Environment
```
API Gateway:      http://api-gateway:8080
Auth Service:     http://auth-service:8081
User Service:     http://user-service:8082
Judge Service:    http://judge-service:8083
AI Assist:        http://ai-assist-service:8084
Metrics Service:  http://metrics-service:8085
Problem Service:  http://problem-service:8086
Config Server:    http://config-server:8888
Eureka Server:    http://eureka:8761
```

## Health Check Endpoints

```bash
# Development
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # User Service
curl http://localhost:8083/actuator/health  # Judge Service
curl http://localhost:8084/actuator/health  # AI Assist
curl http://localhost:8085/actuator/health  # Metrics Service
curl http://localhost:8086/actuator/health  # Problem Service

# Eureka Dashboard
http://localhost:8761
```

## Database Connections

### Development
```bash
# Auth Service Database
psql -h localhost -p 5432 -U postgres -d leetcode_auth

# User Service Database
psql -h localhost -p 5432 -U postgres -d leetcode_users

# Problem Service Database
psql -h localhost -p 5432 -U postgres -d leetcode_problems

# Judge Service Database
psql -h localhost -p 5432 -U postgres -d leetcode_judge

# AI Assist Database
psql -h localhost -p 5432 -U postgres -d leetcode_ai

# Metrics Service Database
psql -h localhost -p 5432 -U postgres -d leetcode_metrics
```

## Service Dependencies

```
Auth Service
├── PostgreSQL (leetcode_auth)
└── Eureka Client

User Service
├── PostgreSQL (leetcode_users)
└── Eureka Client

Problem Service
├── PostgreSQL (leetcode_problems)
└── Eureka Client

Judge Service
├── PostgreSQL (leetcode_judge)
├── RabbitMQ
└── Eureka Client

AI Assist Service
├── PostgreSQL (leetcode_ai)
├── OpenAI API
└── Eureka Client

Metrics Service
├── PostgreSQL (leetcode_metrics)
├── Prometheus
└── Eureka Client
```

## Configuration File Naming Convention

```
<service-name>-<profile>.properties

Examples:
- auth-service-dev.properties      (Development)
- auth-service-k8s.properties      (Kubernetes)
- user-service-dev.properties      (Development)
- user-service-k8s.properties      (Kubernetes)
```

## Testing Configuration

### Verify Config Server
```bash
# Check if config server is running
curl http://localhost:8888/actuator/health

# Get auth-service dev configuration
curl http://localhost:8888/auth-service/dev

# Get user-service k8s configuration
curl http://localhost:8888/user-service/k8s
```

### Verify Service Registration
```bash
# Check Eureka for registered services
curl http://localhost:8761/eureka/apps
```

## Common Issues & Solutions

### Port Already in Use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Service Not Registering with Eureka
1. Check Eureka server is running
2. Verify eureka.client.service-url.defaultZone in config
3. Check network connectivity
4. Review service logs

### Database Connection Failed
1. Verify PostgreSQL is running
2. Check database exists
3. Verify credentials
4. Check connection URL

### Config Server Not Loading Properties
1. Verify Config Server is running
2. Check spring.config.import in application.properties
3. Verify file naming convention
4. Check Config Server logs

## Environment Setup Commands

### Start Development Environment
```bash
# Start PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:latest

# Start RabbitMQ
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:management

# Start services in order
cd infrastructure/eureka-server && mvn spring-boot:run &
cd infrastructure/config-server && mvn spring-boot:run &
cd infrastructure/api-gateway && mvn spring-boot:run &
cd services/auth-service && mvn spring-boot:run &
cd services/user-service && mvn spring-boot:run &
cd services/problem-service && mvn spring-boot:run &
cd services/judge-service && mvn spring-boot:run &
cd services/ai-assist-service && mvn spring-boot:run &
cd services/metrics-service && mvn spring-boot:run &
```

### Create All Databases
```bash
psql -U postgres -c "CREATE DATABASE leetcode_auth;"
psql -U postgres -c "CREATE DATABASE leetcode_users;"
psql -U postgres -c "CREATE DATABASE leetcode_problems;"
psql -U postgres -c "CREATE DATABASE leetcode_judge;"
psql -U postgres -c "CREATE DATABASE leetcode_ai;"
psql -U postgres -c "CREATE DATABASE leetcode_metrics;"
```

## Monitoring

### Prometheus Metrics
```bash
curl http://localhost:8085/actuator/prometheus
```

### Application Metrics
```bash
curl http://localhost:8081/actuator/metrics
curl http://localhost:8082/actuator/metrics
# ... etc for other services
```

## Profile Activation

### Via Environment Variable
```bash
export SPRING_PROFILES_ACTIVE=dev
# or
export SPRING_PROFILES_ACTIVE=k8s
```

### Via Application Properties
```properties
spring.profiles.active=dev
```

### Via Command Line
```bash
java -jar service.jar --spring.profiles.active=k8s
```

