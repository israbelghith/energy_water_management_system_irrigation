# Energy and Water Management System for Irrigation Stations

## Vue d'ensemble

Ce projet implémente une architecture microservices pour la gestion d'énergie et d'eau dans les stations d'irrigation. Il est composé de deux microservices principaux qui communiquent de manière synchrone et asynchrone.

## Architecture

### Microservice Énergie (`energy_service`)
**Port:** 9092

#### Entités
- **Pompe**
  - `id`: Identifiant unique
  - `reference`: Référence de la pompe
  - `puissance`: Puissance en kW
  - `statut`: Statut de la pompe (ACTIVE, INACTIVE, EN_MAINTENANCE, etc.)
  - `dateMiseEnService`: Date de mise en service

- **ConsommationÉlectrique**
  - `id`: Identifiant unique
  - `pompe`: Référence à la pompe
  - `énergieUtilisée`: Énergie utilisée en kWh
  - `durée`: Durée en heures
  - `dateMesure`: Date de la mesure

#### Endpoints REST
- `POST /api/pompes` - Créer une pompe
- `GET /api/pompes` - Récupérer toutes les pompes
- `GET /api/pompes/{id}` - Récupérer une pompe
- `GET /api/pompes/{id}/disponibilite-electrique` - Vérifier la disponibilité électrique
- `PUT /api/pompes/{id}` - Mettre à jour une pompe
- `PATCH /api/pompes/{id}/statut` - Changer le statut d'une pompe
- `DELETE /api/pompes/{id}` - Supprimer une pompe

### Microservice Eau (`eau_service`)
**Port:** 9093

#### Entités
- **Réservoir**
  - `id`: Identifiant unique
  - `nom`: Nom du réservoir
  - `capacitéTotale`: Capacité totale en m³
  - `volumeActuel`: Volume actuel en m³
  - `localisation`: Localisation du réservoir

- **DébitMesuré**
  - `id`: Identifiant unique
  - `pompeId`: Référence à la pompe (du microservice Énergie)
  - `débit`: Débit en m³/h
  - `dateMesure`: Date de la mesure
  - `unité`: Unité de mesure (ex: "m³/h")

#### Endpoints REST

**Réservoirs:**
- `POST /api/reservoirs` - Créer un réservoir
- `GET /api/reservoirs` - Récupérer tous les réservoirs
- `GET /api/reservoirs/{id}` - Récupérer un réservoir
- `GET /api/reservoirs/alertes` - Récupérer les réservoirs en alerte (< 20% capacité)
- `GET /api/reservoirs/localisation/{localisation}` - Récupérer par localisation
- `PUT /api/reservoirs/{id}/volume` - Mettre à jour le volume
- `DELETE /api/reservoirs/{id}` - Supprimer un réservoir

**Débits:**
- `POST /api/debits` - Enregistrer une mesure de débit
- `GET /api/debits/{id}` - Récupérer une mesure
- `GET /api/debits/pompe/{pompeId}` - Récupérer les mesures par pompe
- `GET /api/debits/periode` - Récupérer les mesures par période
- `GET /api/debits/pompe/{pompeId}/moyen` - Calculer le débit moyen
- `GET /api/debits/pompe/{pompeId}/total` - Calculer le débit total
- `DELETE /api/debits/{id}` - Supprimer une mesure

## Fonctionnalités

### 1. Suivi Énergétique
- Enregistrement de la consommation électrique des pompes
- Calcul de la consommation totale par période
- Historique des consommations par pompe

### 2. Alertes sur Consommation Excessive
- Détection automatique de surconsommation (seuil configurable)
- Publication d'événements de surconsommation via RabbitMQ
- Notification asynchrone au microservice Eau

### 3. Optimisation Conjointe Énergie-Eau
- Vérification de disponibilité électrique avant démarrage de pompe
- Suivi des débits d'eau en temps réel
- Alertes sur les réservoirs en dessous de 20% de capacité

## Communication entre Microservices

### Communication Synchrone (Feign Client)
Le microservice **Eau** interroge le microservice **Énergie** via Feign Client pour vérifier la disponibilité électrique avant de démarrer une mesure de débit.

**Exemple:**
```java
// Dans DebitMesureService
Boolean disponibilite = energyServiceClient.verifierDisponibiliteElectrique(pompeId);
```

### Communication Asynchrone (RabbitMQ)
Le microservice **Énergie** publie des événements de surconsommation qui sont consommés par le microservice **Eau**.

**Configuration RabbitMQ:**
- **Queue:** `queueEau`
- **Exchange:** `exchangeEnergy`
- **Routing Key:** `energy.key`

**Événement SurconsommationEvent:**
- `pompeId`: ID de la pompe
- `pompeReference`: Référence de la pompe
- `energieUtilisee`: Énergie consommée
- `seuilDepasse`: Seuil dépassé
- `dateDetection`: Date de détection
- `message`: Message descriptif

## Configuration

### Base de Données
- **energy_service:** MySQL - `energyDB`
- **energy_eau:** MySQL - `eauDB`

### Service Discovery
Les deux microservices s'enregistrent auprès d'Eureka Server sur `http://localhost:8080`

### RabbitMQ
Connexion Cloud AMQP configurée dans `application.properties`

## Démarrage du Projet

### Prérequis
1. Java 17
2. Maven 3.x
3. MySQL Server
4. RabbitMQ (ou compte CloudAMQP)
5. Eureka Server (sur le port 8080)

### Étapes de Démarrage

1. **Démarrer MySQL**
   ```bash
   # Les bases de données seront créées automatiquement
   ```

2. **Démarrer Eureka Server**
   ```bash
   cd MSEureka
   mvn spring-boot:run
   ```

3. **Démarrer le microservice Énergie**
   ```bash
   cd energy_service
   mvn spring-boot:run
   ```

4. **Démarrer le microservice Eau**
   ```bash
   cd eau_service
   mvn spring-boot:run
   ```

## Tester les Fonctionnalités

### Test de Communication Synchrone

1. Créer une pompe dans le microservice Énergie:
```bash
curl -X POST http://localhost:9092/api/pompes \
  -H "Content-Type: application/json" \
  -d '{
    "reference": "POMPE-001",
    "puissance": 75.0,
    "statut": "ACTIVE",
    "dateMiseEnService": "2024-01-01"
  }'
```

2. Créer une mesure de débit (qui vérifie automatiquement la disponibilité électrique):
```bash
curl -X POST http://localhost:9093/api/debits \
  -H "Content-Type: application/json" \
  -d '{
    "pompeId": 1,
    "debit": 150.0,
    "unite": "m3/h",
    "dateMesure": "2024-12-23T10:00:00"
  }'
```

### Test de Communication Asynchrone

1. Créer une consommation électrique dépassant le seuil (100 kWh):
```bash
curl -X POST http://localhost:9092/api/consommations \
  -H "Content-Type: application/json" \
  -d '{
    "pompe": {"id": 1},
    "energieUtilisee": 150.0,
    "duree": 2.0,
    "dateMesure": "2024-12-23T10:00:00"
  }'
```

2. Vérifier les logs du microservice Eau pour voir l'événement reçu

## Technologies Utilisées

- **Spring Boot 4.0**
- **Spring Cloud (Netflix Eureka, OpenFeign)**
- **Spring Data JPA**
- **Spring AMQP (RabbitMQ)**
- **MySQL**
- **Lombok**
- **Maven**

## Structure du Projet

```
eau_service/
├── src/main/java/tn/isra/belghith/
│   ├── clients/              # Feign Clients
│   ├── config/               # Configurations (RabbitMQ)
│   ├── controllers/          # REST Controllers
│   ├── dto/                  # Data Transfer Objects
│   ├── entities/             # Entités JPA
│   ├── events/               # Événements (SurconsommationEvent)
│   ├── repositories/         # JPA Repositories
│   └── services/             # Services métier
└── src/main/resources/
    └── application.properties

energy_service/
├── src/main/java/tn/isra/belghith/
│   ├── config/               # Configurations (RabbitMQ)
│   ├── controllers/          # REST Controllers
│   ├── DTO/                  # Data Transfer Objects
│   ├── entities/             # Entités JPA
│   ├── events/               # Événements
│   ├── repositories/         # JPA Repositories
│   └── services/             # Services métier
└── src/main/resources/
    └── application.properties
```

## Améliorations Futures

- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Implémenter un API Gateway
- [ ] Ajouter Spring Cloud Config Server
- [ ] Implémenter Circuit Breaker (Resilience4j)
- [ ] Ajouter des métriques avec Micrometer/Prometheus
- [ ] Implémenter un système d'authentification (OAuth2/JWT)
- [ ] Ajouter une interface utilisateur (React/Angular)
- [ ] Dockeriser les microservices

## Auteur

Projet développé pour le cours de microservices - ISI
