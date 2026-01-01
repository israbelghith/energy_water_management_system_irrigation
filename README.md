# 🌱 Système de Gestion d'Irrigation - Energy & Water Management

## 📋 Description du Projet

Application web basée sur une architecture microservices pour la gestion intelligente de l'irrigation agricole. Le système intègre la gestion de l'énergie électrique et de l'eau, permettant un contrôle optimal des pompes d'irrigation, le suivi de la consommation électrique, la gestion des réservoirs et la mesure des débits.

## 🏗️ Architecture Microservices

### Microservices Métiers
1. **energy-service** (Port 9092)
   - Gestion des pompes d'irrigation
   - Suivi de la consommation électrique
   - Vérification de la disponibilité électrique
   - Détection de surconsommation avec alertes

2. **eau-service** (Port 9093)
   - Gestion des réservoirs d'eau
   - Mesure et suivi des débits
   - Système d'alerte pour niveau d'eau critique
   - Communication avec energy-service pour vérification électrique

### Microservices Architecturaux
3. **gateway** (Port 9095)
   - Spring Cloud Gateway
   - Routage centralisé (paths: `/energy/**`, `/eau/**`)
   - Gestion CORS globale
   - RewritePath filters

4. **MSEureka** (Port 8080)
   - Service Discovery avec Eureka Server
   - Enregistrement automatique des microservices

5. **ConfigServer** (Port 9999)
   - Configuration centralisée
   - Repository Git pour les configurations

## 💻 Technologies Utilisées

### Backend
- **Framework**: Spring Boot 3.4.0
- **Language**: Java 17
- **Databases**: MySQL 8.0
  - energyDB (energy-service)
  - eauDB (eau-service)
- **Communication**:
  - Synchrone: OpenFeign
  - Asynchrone: RabbitMQ (AMQP)
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config

### Frontend
- **Framework**: Angular 19 (Standalone Components)
- **HTTP Client**: HttpClient
- **Routing**: Angular Router
- **Styling**: CSS3 with Bootstrap

### DevOps
- **Containerization**: Docker
- **Orchestration**: Kubernetes (templates inclus)
- **Build Tool**: Maven (backend), npm (frontend)

## 📁 Structure du Projet

```
energy_water_management_system_irrigation/
├── ConfigServer/           # Configuration centralisée
├── MSEureka/              # Service Discovery
├── gateway/               # API Gateway
├── energy_service/        # Microservice énergie
│   ├── src/main/java/tn/isra/belghith/
│   │   ├── controllers/   # REST Controllers
│   │   ├── services/      # Business Logic
│   │   ├── repositories/  # Data Access
│   │   ├── entities/      # JPA Entities
│   │   ├── DTO/          # Data Transfer Objects
│   │   └── events/       # Event Models
│   └── Dockerfile
├── eau_service/           # Microservice eau
│   ├── src/main/java/tn/isra/belghith/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── entities/
│   │   ├── dto/
│   │   ├── clients/      # Feign Clients
│   │   └── events/
│   └── Dockerfile
├── frontend/              # Application Angular (branche develop-frontend)
│   ├── src/app/
│   │   ├── components/   # UI Components
│   │   ├── services/     # HTTP Services
│   │   └── models/       # TypeScript Models
│   └── Dockerfile
├── kubernetes/            # Templates Kubernetes (à créer)
└── README.md             # Ce fichier
```

## 🚀 Installation et Exécution

### Prérequis
- Java 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0
- RabbitMQ 3.12+
- Docker & Docker Compose (optionnel)

### Configuration des Bases de Données

```sql
CREATE DATABASE energyDB;
CREATE DATABASE eauDB;
```

### Démarrage Manuel (Développement)

1. **Démarrer ConfigServer**
```bash
cd ConfigServer
mvn spring-boot:run
```

2. **Démarrer Eureka**
```bash
cd MSEureka
mvn spring-boot:run
```

3. **Démarrer les Microservices**
```bash
# Energy Service
cd energy_service
mvn spring-boot:run

# Eau Service
cd eau_service
mvn spring-boot:run
```

4. **Démarrer Gateway**
```bash
cd gateway
mvn spring-boot:run
```

5. **Démarrer Frontend** (basculer sur branche develop-frontend)
```bash
git checkout develop-frontend
cd frontend
npm install
npm start
```

**URLs d'accès:**
- Frontend: http://localhost:4200
- Gateway: http://localhost:9095
- Eureka Dashboard: http://localhost:8080
- Energy Service: http://localhost:9092
- Eau Service: http://localhost:9093

### Démarrage avec Docker

```bash
# Build des images
docker build -t config-server ./ConfigServer
docker build -t eureka-server ./MSEureka
docker build -t energy-service ./energy_service
docker build -t eau-service ./eau_service
docker build -t gateway ./gateway

# Frontend (sur branche develop-frontend)
git checkout develop-frontend
docker build -t frontend ./frontend

# Démarrage avec docker-compose
docker-compose up -d
```

### Déploiement Kubernetes

```bash
# Appliquer les configurations
kubectl apply -f kubernetes/

# Vérifier le déploiement
kubectl get pods
kubectl get services
```

## 🔌 API Endpoints

### Energy Service (via Gateway: /energy)

#### Pompes
- `GET /energy/api/pompes` - Liste toutes les pompes
- `GET /energy/api/pompes/{id}` - Détails d'une pompe
- `POST /energy/api/pompes` - Créer une pompe
- `PUT /energy/api/pompes/{id}` - Modifier une pompe
- `PUT /energy/api/pompes/{id}/activer` - Activer une pompe
- `PUT /energy/api/pompes/{id}/desactiver` - Désactiver une pompe
- `DELETE /energy/api/pompes/{id}` - Supprimer une pompe

#### Consommations
- `GET /energy/api/consommations` - Liste toutes les consommations
- `GET /energy/api/consommations/total` - Consommation totale globale
- `POST /energy/api/consommations` - Enregistrer une consommation
- `GET /energy/api/consommations/pompe/{pompeId}` - Consommations par pompe
- `GET /energy/api/consommations/surconsommation/{seuil}` - Surconsommations

### Eau Service (via Gateway: /eau)

#### Réservoirs
- `GET /eau/api/reservoirs` - Liste tous les réservoirs
- `GET /eau/api/reservoirs/{id}` - Détails d'un réservoir
- `POST /eau/api/reservoirs` - Créer un réservoir
- `PUT /eau/api/reservoirs/{id}` - Modifier un réservoir
- `PUT /eau/api/reservoirs/{id}/volume` - Mettre à jour le volume
- `GET /eau/api/reservoirs/alertes` - Réservoirs en alerte
- `DELETE /eau/api/reservoirs/{id}` - Supprimer un réservoir

#### Débits
- `GET /eau/api/debits` - Liste toutes les mesures de débit
- `POST /eau/api/debits` - Enregistrer une mesure
- `GET /eau/api/debits/verifier-energie/{pompeId}` - Vérifier disponibilité électrique
- `GET /eau/api/debits/pompe/{pompeId}` - Débits par pompe
- `GET /eau/api/debits/pompe/{pompeId}/moyen` - Débit moyen d'une pompe
- `DELETE /eau/api/debits/{id}` - Supprimer une mesure

## 🔄 Communication Inter-Services

### Synchrone (Feign Client)
- **eau-service → energy-service**: Vérification de la disponibilité électrique avant démarrage de pompe
  ```java
  @FeignClient(name = "energy-service", url = "http://localhost:9092")
  Boolean verifierDisponibiliteElectrique(@PathVariable Long pompeId);
  ```

### Asynchrone (RabbitMQ)
- **energy-service → eau-service**: Notification de surconsommation électrique
  ```
  Exchange: irrigation.exchange
  Routing Key: irrigation.surconsommation
  Queue: irrigation.eau.queue
  ```

## ⚙️ Fonctionnalités Principales

### 1. Gestion des Pompes
- CRUD complet
- Activation/Désactivation
- Suivi de l'état et de la puissance
- Association avec consommations électriques

### 2. Suivi Énergétique
- Enregistrement des consommations
- Calcul de consommation totale
- Détection de surconsommation automatique
- Alertes via RabbitMQ

### 3. Gestion de l'Eau
- Gestion des réservoirs avec alertes de niveau bas
- Mesure des débits en temps réel
- Calcul de débits moyens
- Vérification électrique avant pompage

### 4. Architecture Résiliente
- Fallback Feign pour tolérance aux pannes
- Service Discovery pour scalabilité
- Configuration centralisée
- CORS géré au niveau Gateway

## 🧪 Tests

```bash
# Backend
cd energy_service
mvn test

cd eau_service
mvn test

# Frontend (branche develop-frontend)
git checkout develop-frontend
cd frontend
npm test
```

## 📊 Monitoring

- **Eureka Dashboard**: http://localhost:8080
- **Spring Boot Actuator**: Activé sur tous les microservices
  - `/actuator/health`
  - `/actuator/info`
  - `/actuator/metrics`

## 🔐 Sécurité

- CORS configuré au niveau Gateway
- Validation des données côté backend
- Gestion des erreurs centralisée

## 📝 Conventions Git

### Branches
- `main`: Production
- `develop-backend`: Développement backend (microservices Spring Boot)
- `develop-frontend`: Développement frontend (Angular 19)

### Commits
Format: `type(scope): description`
- **feat**: Nouvelle fonctionnalité
- **fix**: Correction de bug
- **config**: Configuration
- **docs**: Documentation
- **refactor**: Refactoring

## 👥 Auteurs

Projet académique - Gestion d'Irrigation Intelligente

## 📄 Licence

Ce projet est développé dans un cadre académique.

---

**Note**: Le frontend est disponible sur la branche `develop-frontend`. Pour y accéder: `git checkout develop-frontend`
