# 🚀 LeetCode Clone - Plateforme de Coding Challenges

Une plateforme moderne de coding challenges inspirée de LeetCode, construite avec une architecture microservices Spring Boot et Angular.

## 📋 Table des Matières

- [Architecture](#architecture)
- [Technologies](#technologies)
- [Services](#services)
- [Démarrage Rapide](#démarrage-rapide)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Développement](#développement)
- [Déploiement](#déploiement)

## 🏗️ Architecture

Architecture microservices avec les composants suivants :

```
┌─────────────┐
│   Angular   │
│   Frontend  │
└──────┬──────┘
       │
┌──────▼──────────────────────────────────┐
│      API Gateway (Port 8080)            │
│  JWT Authentication + Rate Limiting     │
└──────┬──────────────────────────────────┘
       │
┌──────▼──────────────────────────────────┐
│    Eureka Discovery (Port 8761)         │
└──────┬──────────────────────────────────┘
       │
       ├───► Auth Service (8081)
       ├───► User Service (8082)
       ├───► Judge Service (8083)
       ├───► AI Assist (8084)
       └───► Problem Service (8086)
```

### Flux d'Exécution de Code

```
User → API Gateway → Judge Service → Docker Execution Engine
                          ↓
                    Problem Service (Test Cases)
                          ↓
                    Submission Result → User Service (Score Update)
```

## 💻 Technologies

### Backend
- **Java 21** avec Spring Boot 3.3.4
- **Spring Cloud** (Eureka, Gateway, Config Server)
- **PostgreSQL 15** - Base de données
- **Redis 7** - Cache et gestion de tokens
- **RabbitMQ 3.12** - Message broker
- **Docker** - Exécution sécurisée du code
- **Prometheus + Grafana** - Monitoring

### Frontend (À venir)
- **Angular 17+**
- **TypeScript**
- **TailwindCSS**

## 🔧 Services

### Infrastructure Services

#### 1. Eureka Server (8761)
Service Discovery pour tous les microservices.
- Dashboard: http://localhost:8761

#### 2. Config Server (8888)
Configuration centralisée via Vault + Git.

#### 3. API Gateway (8080)
- Point d'entrée unique
- Authentification JWT
- Rate limiting avec Redis
- Routing vers microservices

### Business Services

#### 4. Auth Service (8081)
Gestion de l'authentification et des utilisateurs.

**Endpoints:**
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion
- `GET /api/auth/verify?token=` - Vérification email
- `POST /api/auth/refresh-token` - Renouvellement token
- `POST /api/auth/forget-password` - Demande reset mot de passe
- `POST /api/auth/reset-password?token=` - Reset mot de passe
- `POST /api/auth/logout` - Déconnexion

#### 5. User Service (8082)
Profils utilisateurs, scores, et notes.

**Endpoints:**
- `GET /api/users/me` - Mon profil
- `PUT /api/users/me` - Mettre à jour profil
- `GET /api/users/{id}` - Profil public
- `POST /api/notes` - Sauvegarder note
- `GET /api/notes/{problemId}` - Récupérer note

#### 6. Problem Service (8086)
Gestion des problèmes de coding.

**Endpoints:**
- `GET /api/problems` - Liste paginée
- `GET /api/problems/{slug}` - Détails problème
- `POST /api/problems` - Créer problème (Admin)
- `DELETE /api/problems/{id}` - Supprimer problème

#### 7. Judge Service (8083)
Exécution et évaluation du code.

**Endpoints:**
- `POST /api/submissions` - Soumettre code
- `GET /api/submissions/me` - Mon historique paginé
- `GET /api/submissions/me/problem/{id}` - Soumissions par problème
- `GET /api/submissions/{id}` - Détails soumission

**Langages supportés:**
- ✅ Java
- ✅ Python
- ✅ JavaScript
- ✅ C++
- ✅ Go

#### 8. Notification Service
Envoi d'emails asynchrone via RabbitMQ.

#### 9. AI Assist Service (8084)
Assistance IA pour les problèmes (en cours).

## 🚀 Démarrage Rapide

### Prérequis

- Java 21
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15 (ou via Docker)
- Node.js 18+ (pour le frontend)

### Installation

1. **Cloner le repository**
```bash
git clone https://github.com/yourusername/leetcode-clone.git
cd leetcode-clone
```

2. **Configurer les variables d'environnement**
```bash
cp .env.example .env
# Éditer .env avec vos configurations
```

3. **Démarrer l'infrastructure avec Docker Compose**
```bash
docker-compose up -d postgres redis rabbitmq
```

4. **Compiler tous les microservices**
```bash
mvn clean install
```

5. **Démarrer les services (ordre important)**
```bash
# 1. Eureka Server
cd infrastructure/eureka-server
mvn spring-boot:run

# 2. Config Server (nouveau terminal)
cd infrastructure/config-server
mvn spring-boot:run

# 3. API Gateway (nouveau terminal)
cd infrastructure/api-gateway
mvn spring-boot:run

# 4. Business Services (nouveaux terminaux)
cd services/auth-service && mvn spring-boot:run
cd services/user-service && mvn spring-boot:run
cd services/problem-service && mvn spring-boot:run
cd services/judge-service && mvn spring-boot:run
cd services/notification-service && mvn spring-boot:run
```

### Démarrage complet avec Docker

```bash
docker-compose up -d
```

## ⚙️ Configuration

### Base de données

Les schémas sont créés automatiquement par JPA (Hibernate).

**Schémas principaux:**
- `users` - Authentification
- `user_profiles` - Profils utilisateurs
- `problems` - Problèmes de coding
- `test_cases` - Cas de test
- `submissions` - Soumissions de code
- `submission_results` - Résultats détaillés

### Redis

Utilisé pour:
- Cache de tokens JWT
- Rate limiting
- Sessions utilisateurs

### RabbitMQ

**Exchanges et Queues:**
- `auth.exchange` → `notification.email.register.queue` (Emails vérification)
- `auth.exchange` → `user.profile.queue` (Création profils)
- Dead Letter Queue (DLQ) pour gestion erreurs

### LLM Configuration (AI Assist Service)

Le AI Assist Service supporte 3 providers LLM configurables.

**Sélectionner un provider:**
```properties
# Dans services/ai-assist-service/src/main/resources/application.properties
llm.provider=deepseek    # ou gemini ou openai
```

**Variables d'environnement requises:**
```bash
# DeepSeek (Recommandé - Gratuit)
DEEPSEEK_API_KEY=sk-your-deepseek-key

# Gemini (Alternative gratuite)
GEMINI_API_KEY=AIzaSy-your-gemini-key

# OpenAI (Payant)
OPENAI_API_KEY=sk-your-openai-key
```

**Obtenir les API keys:**
- DeepSeek: https://platform.deepseek.com (gratuit, 60 req/min)
- Gemini: https://makersuite.google.com/app/apikey (gratuit)
- OpenAI: https://platform.openai.com (payant, ~$0.002/1K tokens)

**Documentation complète:** `services/ai-assist-service/README.md`

## 📚 API Documentation

### Authentification

Toutes les requêtes sauf `/api/auth/*` nécessitent un token JWT.

**Header requis:**
```
Authorization: Bearer <your-jwt-token>
```

### Exemples de requêtes

**1. Inscription**
```bash
curl -X POST http://localhost:8080/auth-service/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!",
    "displayName": "John Doe"
  }'
```

**2. Connexion**
```bash
curl -X POST http://localhost:8080/auth-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }'
```

**3. Récupérer problèmes**
```bash
curl http://localhost:8080/problem-service/api/problems?page=0&size=20 \
  -H "Authorization: Bearer <token>"
```

**4. Soumettre code**
```bash
curl -X POST http://localhost:8080/judge-service/api/submissions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "problemId": "two-sum",
    "language": "python",
    "code": "def twoSum(nums, target):\n    return [0, 1]"
  }'
```

### OpenAPI/Swagger

Documentation interactive disponible:
- API Gateway: http://localhost:8080/swagger-ui.html
- Auth Service: http://localhost:8081/swagger-ui.html
- Problem Service: http://localhost:8086/swagger-ui.html

## 🛠️ Développement

### Structure du projet

```
leetcode-clone/
├── infrastructure/           # Services d'infrastructure
│   ├── api-gateway/
│   ├── eureka-server/
│   ├── config-server/
│   └── monitoring/          # Prometheus + Grafana
├── services/                # Microservices métier
│   ├── auth-service/
│   ├── user-service/
│   ├── problem-service/
│   ├── judge-service/
│   ├── notification-service/
│   └── ai-assist-service/
├── argocd/                  # Manifestes Kubernetes
├── ci-cd/                   # GitHub Actions
├── docs/                    # Documentation
└── docker-compose.yml
```

### Ajouter un nouveau langage de programmation

1. Créer une nouvelle Strategy dans `judge-service/infrastructure/execution/strategy/`
```java
@Component
public class JavaScriptStrategy implements ILanguagesStrategy {
    @Override
    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.JAVASCRIPT;
    }
    
    @Override
    public String getDockerImage() {
        return "node:18-alpine";
    }
    
    @Override
    public String getRunCommand() {
        return "node solution.js";
    }
    
    // ... autres méthodes
}
```

2. Ajouter l'enum dans `ProgrammingLanguage.java`

### Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify

# Tests spécifiques à un service
cd services/auth-service
mvn test
```

## 📦 Déploiement

### Kubernetes avec ArgoCD

Les manifestes sont dans le dossier `argocd/`.

```bash
# Appliquer les manifestes
kubectl apply -f argocd/

# Vérifier le déploiement
kubectl get pods -n leetcode
```

### CI/CD avec GitHub Actions

Les pipelines sont configurés dans `.github/workflows/` (à créer).

## 📊 Monitoring

### Prometheus
- URL: http://localhost:9090
- Scrape les endpoints `/actuator/prometheus` de tous les services

### Grafana
- URL: http://localhost:3000
- Credentials: admin/admin
- Dashboards pour métriques Spring Boot

## 🔐 Sécurité

- ✅ JWT avec expiration
- ✅ Tokens révocables via Redis
- ✅ Isolation Docker pour exécution de code
- ✅ Rate limiting
- ✅ Validation des entrées
- ✅ HTTPS ready (TLS termination au Gateway)

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 License

MIT License - voir LICENSE pour détails

## 👥 Auteurs

- Votre Nom - [@yourhandle](https://github.com/yourhandle)

## 🙏 Remerciements

- LeetCode pour l'inspiration
- Spring Boot Community
- Docker pour l'exécution sécurisée du code

