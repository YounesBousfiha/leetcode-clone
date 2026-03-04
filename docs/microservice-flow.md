# 🔄 Microservices Flow Documentation

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client (Browser)                         │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway (8080)                          │
│  - JWT Validation (Redis)                                       │
│  - Rate Limiting                                                 │
│  - Routing                                                       │
└────────────┬────────────────────────────────────────────────────┘
             │
             ├─────────────► Eureka Server (8761)
             │                Service Discovery
             │
             ├─────────────► Config Server (8888)
             │                Centralized Configuration
             │
        ┌────┴────┬──────────┬──────────┬──────────┬─────────┐
        │         │          │          │          │         │
        ▼         ▼          ▼          ▼          ▼         ▼
    ┌───────┐ ┌──────┐  ┌────────┐ ┌──────┐  ┌───────┐ ┌───────┐
    │ Auth  │ │ User │  │Problem │ │Judge │  │ AI    │ │Notify │
    │Service│ │Service│ │Service │ │Service│ │Assist │ │Service│
    │ 8081  │ │ 8082 │  │ 8086   │ │ 8083 │  │ 8084  │ │       │
    └───┬───┘ └───┬──┘  └───┬────┘ └───┬──┘  └───┬───┘ └───┬───┘
        │         │          │          │          │         │
        └────┬────┴──────────┴──────────┴──────────┴────┬────┘
             │                                            │
        ┌────▼────┐  ┌──────────┐  ┌────────┐      ┌────▼────┐
        │PostgreSQL│  │  Redis   │  │RabbitMQ│      │  OpenAI │
        │  5432   │  │  6379    │  │  5672  │      │   API   │
        └─────────┘  └──────────┘  └────────┘      └─────────┘
```

---

## 1️⃣ User Registration Flow

```
Client                API Gateway         Auth Service        RabbitMQ           User Service      Notification Service
  │                       │                    │                 │                    │                    │
  ├──POST /register──────►│                    │                 │                    │                    │
  │                       ├──JWT Check (Skip)─►│                 │                    │                    │
  │                       │                    ├─Create User─────┤                    │                    │
  │                       │                    ├─Generate Token──┤                    │                    │
  │                       │                    │                 │                    │                    │
  │                       │                    ├─Publish Event──►│                    │                    │
  │                       │                    │  (UserRegistered)│                   │                    │
  │                       │                    │                 ├──Consume Event────►│                    │
  │                       │                    │                 │                    ├─Create Profile─────┤
  │                       │                    │                 │                    │                    │
  │                       │                    │                 ├──Consume Event────┤────────────────────►│
  │                       │                    │                 │  (EmailVerification)                    │
  │                       │                    │                 │                    │                    ├─Send Email──►
  │◄──201 Created─────────┤◄───Response────────┤                 │                    │                    │
  │  "Check your email"   │                    │                 │                    │                    │
```

**Steps:**
1. User submits registration form
2. API Gateway forwards to Auth Service
3. Auth Service creates user and generates verification token
4. Publishes `UserRegisteredEvent` to RabbitMQ
5. User Service consumes event and creates profile
6. Notification Service consumes event and sends verification email
7. User receives email with verification link

---

## 2️⃣ User Login Flow

```
Client              API Gateway         Auth Service         Redis
  │                     │                    │                 │
  ├──POST /login───────►│                    │                 │
  │  {email, password}  │                    │                 │
  │                     ├──JWT Check (Skip)─►│                 │
  │                     │                    ├─Verify Password─┤
  │                     │                    ├─Check Verified──┤
  │                     │                    ├─Generate JWT────┤
  │                     │                    ├─Store in Redis─►│
  │                     │                    │  (access + refresh)
  │◄──200 OK────────────┤◄───Response────────┤                 │
  │  {accessToken,      │                    │                 │
  │   refreshToken}     │                    │                 │
```

**Token Structure:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "uuid"
}
```

**Redis Keys:**
- `jwt:access:{userId}` → Access Token
- `jwt:refresh:{userId}` → Refresh Token

---

## 3️⃣ Authenticated Request Flow

```
Client              API Gateway         Redis            Target Service
  │                     │                 │                    │
  ├──GET /problems─────►│                 │                    │
  │  Authorization:     │                 │                    │
  │  Bearer <token>     │                 │                    │
  │                     ├─Extract Token───┤                    │
  │                     ├─Validate JWT────┤                    │
  │                     ├─Check Redis─────►│                   │
  │                     │                 │ (token exists?)    │
  │                     │                 ◄─────────────────────┤
  │                     ├─Extract User-Id─┤                    │
  │                     ├─Add Header──────┤                    │
  │                     │  X-User-Id      │                    │
  │                     ├─Forward Request─────────────────────►│
  │                     │                 │                    ├─Process─┤
  │◄──200 OK────────────┤◄────Response─────────────────────────┤         │
```

**Gateway Filters:**
1. `RouteValidator` - Check if route needs auth
2. `AuthenticationFilter` - Validate JWT
3. `JwtUtil` - Decode and verify token
4. `RedisService` - Check token validity

---

## 4️⃣ Code Submission & Execution Flow

```
Client          API Gateway      Judge Service    Problem Service   Docker Engine    User Service
  │                 │                  │                 │                │               │
  ├─POST /submit───►│                  │                 │                │               │
  │  {problemId,    │                  │                 │                │               │
  │   code,         │                  │                 │                │               │
  │   language}     │                  │                 │                │               │
  │                 ├─Auth Check──────►│                 │                │               │
  │                 │                  ├─Save Submission─┤                │               │
  │                 │                  ├─Get Test Cases─►│                │               │
  │                 │                  │◄────────────────┤                │               │
  │                 │                  │                 │                │               │
  │                 │                  ├─For each test case:              │               │
  │                 │                  ├─Wrap Code───────┤                │               │
  │                 │                  ├─Create Container────────────────►│               │
  │                 │                  │                 │  docker run    │               │
  │                 │                  │                 │  --memory=256m │               │
  │                 │                  │                 │  --cpus=0.5    │               │
  │                 │                  │◄─Execution Result───────────────┤               │
  │                 │                  ├─Compare Output──┤                │               │
  │                 │                  ├─Store Result────┤                │               │
  │                 │                  │                 │                │               │
  │                 │                  ├─Calculate Status┤                │               │
  │                 │                  │  (ACCEPTED?)    │                │               │
  │                 │                  ├─Update Score────────────────────────────────────►│
  │                 │                  │  (if ACCEPTED)  │                │               │
  │◄─200 OK─────────┤◄─Result──────────┤                 │                │               │
  │  {status,       │                  │                 │                │               │
  │   executionTime,│                  │                 │                │               │
  │   details[]}    │                  │                 │                │               │
```

**Language Strategies:**
- **Java**: `openjdk:17-alpine` → compile + execute
- **Python**: `python:3.9-alpine` → direct execution
- **JavaScript**: `node:18-alpine` → direct execution
- **C++**: `gcc:12-alpine` → compile with g++
- **Go**: `golang:1.21-alpine` → go run

**Docker Execution:**
```bash
docker run --rm \
  --memory=256m \
  --cpus=0.5 \
  -v /temp/uuid:/app \
  -w /app \
  python:3.9-alpine \
  python3 main.py
```

---

## 5️⃣ Leaderboard Update Flow

```
Judge Service       User Service      PostgreSQL
      │                   │               │
      ├─Submission────────►│               │
      │  (ACCEPTED)        │               │
      │  {userId,          │               │
      │   points,          │               │
      │   difficulty}      │               │
      │                    ├─Begin TX──────►│
      │                    ├─Update Score───►│
      │                    │  user_profiles │
      │                    ├─Update Stats───►│
      │                    │  user_statistics│
      │                    ├─Commit TX──────►│
      │◄───Success─────────┤               │
```

**Score Calculation:**
- Easy: 5 points
- Medium: 10 points
- Hard: 15 points

**Statistics Updated:**
- Total problems solved
- Difficulty breakdown
- Acceptance rate
- Current streak
- Longest streak

---

## 6️⃣ Problem Filtering Flow

```
Client          API Gateway      Problem Service    PostgreSQL
  │                 │                  │                │
  ├─GET /filter────►│                  │                │
  │  ?difficulty=   │                  │                │
  │   MEDIUM        │                  │                │
  │  &tags=array,dp │                  │                │
  │  &page=0&size=20│                  │                │
  │                 ├─Auth Check──────►│                │
  │                 │                  ├─Build Query────►│
  │                 │                  │  SELECT p.*    │
  │                 │                  │  FROM problems p│
  │                 │                  │  JOIN tags t   │
  │                 │                  │  WHERE p.diff= │
  │                 │                  │    'MEDIUM'    │
  │                 │                  │  AND t.slug IN │
  │                 │                  │    ('array','dp')│
  │                 │                  │  LIMIT 20      │
  │                 │                  │  OFFSET 0      │
  │                 │                  ◄────────────────┤
  │◄─200 OK─────────┤◄─Results─────────┤                │
  │  {content[],    │                  │                │
  │   totalPages,   │                  │                │
  │   totalElements}│                  │                │
```

**Supported Filters:**
- `difficulty`: EASY | MEDIUM | HARD
- `tags`: Array of tag slugs
- `keyword`: Full-text search

---

## 7️⃣ AI Hint Generation Flow (Planned)

```
Client          API Gateway      AI Service       OpenAI API      Problem Service
  │                 │                │                │                 │
  ├─POST /hint─────►│                │                │                 │
  │  {problemSlug,  │                │                │                 │
  │   userCode}     │                │                │                 │
  │                 ├─Auth Check────►│                │                 │
  │                 ├─Rate Limit─────►│                │                 │
  │                 │  (Redis)       │                │                 │
  │                 │                ├─Get Problem───────────────────►│
  │                 │                │◄──────────────────────────────┤
  │                 │                ├─Build Prompt───┤                 │
  │                 │                ├─Call OpenAI───►│                 │
  │                 │                │                ├─GPT-4──►        │
  │                 │                │◄───Response────┤                 │
  │                 │                ├─Parse Result───┤                 │
  │◄─200 OK─────────┤◄───Hint────────┤                │                 │
  │  {hint,         │                │                │                 │
  │   approach,     │                │                │                 │
  │   complexity}   │                │                │                 │
```

---

## 8️⃣ Service-to-Service Communication

### Feign Client (Synchronous)

**Judge Service → Problem Service:**
```java
@FeignClient(name = "problem-service")
public interface ProblemFeignClient {
    @GetMapping("/api/problems/internal/{slug}")
    ProblemDetailResponse getProblem(@PathVariable String slug);
}
```

### RabbitMQ (Asynchronous)

**Events:**
- `UserRegisteredEvent` → User Service, Notification Service
- `PasswordResetEvent` → Notification Service
- `SubmissionAcceptedEvent` → User Service (future)

**Queue Configuration:**
```
Exchange: auth.exchange (TopicExchange)
├─ Routing Key: auth.user.registered
│  └─ Queue: user.profile.queue → User Service
│      └─ DLQ: user.profile.dlq (Dead Letter Queue)
│
└─ Routing Key: auth.user.registered
   └─ Queue: notification.email.register.queue → Notification Service
```

---

## 9️⃣ Error Handling Strategy

### API Gateway
- 401 Unauthorized → Token missing/invalid
- 403 Forbidden → Token expired
- 429 Too Many Requests → Rate limit exceeded

### Business Services
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(Exception ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
    }
    
    // Other exception handlers...
}
```

**Standard Error Response:**
```json
{
  "timestamp": "2026-03-04T10:30:00",
  "status": 404,
  "title": "Resource Not Found",
  "detail": "Problem with slug 'two-sum' not found",
  "path": "/api/problems/two-sum"
}
```

---

## 🔟 Monitoring & Observability

### Actuator Endpoints
All services expose:
- `/actuator/health` - Health check
- `/actuator/prometheus` - Metrics for Prometheus
- `/actuator/info` - Service info

### Prometheus Scraping
```yaml
scrape_configs:
  - job_name: 'auth-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
```

### Key Metrics
- `http_server_requests_seconds` - Request latency
- `jvm_memory_used_bytes` - Memory usage
- `submission_execution_time_seconds` - Code execution time
- `rabbitmq_messages_published_total` - Message count

---

## Database Schema Overview

### Auth Service (PostgreSQL)
```sql
users (id, email, password_hash, role, is_verified)
email_verification (id, user_id, token, expires_at)
password_reset_tokens (id, user_id, token, expires_at)
```

### User Service (PostgreSQL)
```sql
user_profiles (id, email, display_name, bio, score)
user_statistics (user_id, total_solved, easy_solved, ...)
user_notes (id, user_id, problem_id, content)
```

### Problem Service (PostgreSQL)
```sql
problems (id, title, slug, description, difficulty)
test_cases (id, problem_id, input, expected_output)
tags (id, name, slug)
problem_tags (problem_id, tag_id)
code_templates (id, problem_id, language, boilerplate)
```

### Judge Service (PostgreSQL)
```sql
submissions (id, user_id, problem_id, code, status)
submission_results (id, submission_id, test_case_id, passed)
```

---

## Security Considerations

1. **JWT Tokens**: Short-lived (1h), stored in Redis
2. **Password**: BCrypt hashed with salt
3. **Code Execution**: Isolated Docker containers with resource limits
4. **Rate Limiting**: Redis-based per user/IP
5. **HTTPS**: TLS termination at Gateway
6. **CORS**: Configured at Gateway level

---

## Performance Optimization

1. **Caching**: Redis for JWT, user profiles
2. **Database Indexing**: On slug, userId, createdAt
3. **Pagination**: All list endpoints
4. **Async Processing**: RabbitMQ for emails
5. **Connection Pooling**: HikariCP
6. **Docker Cleanup**: Temp files deleted after execution

---

## Future Enhancements

- [ ] WebSocket for real-time submission results
- [ ] GraphQL API for flexible queries
- [ ] Kafka for event streaming
- [ ] Elasticsearch for full-text search
- [ ] Circuit Breaker pattern (Resilience4j)
- [ ] Distributed tracing (Jaeger/Zipkin)

