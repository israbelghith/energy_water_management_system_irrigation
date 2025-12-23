# Energy and Water Management System for Irrigation Stations

This project implements a microservices architecture for managing energy and water in irrigation stations.

## 🏗️ Architecture

This system consists of 3 microservices:

### 1. **energy-service** (Port: 9092)
Energy management microservice that handles:
- Pump management (pumps, power, status)
- Electrical consumption tracking
- Overconsumption detection and alerts
- Publishing events to RabbitMQ

### 2. **eau-service** (Port: 9093)
Water management microservice that handles:
- Reservoir management (capacity, current volume, location)
- Flow rate measurement
- Synchronous communication with energy-service (electrical availability check)
- Consuming overconsumption events from RabbitMQ

### 3. **MSEureka** (Port: 8080)
Service Discovery server using Netflix Eureka for microservices registration and discovery.

## 🔄 Communication

### Synchronous Communication (OpenFeign)
- **eau-service** queries **energy-service** to verify electrical availability before starting a pump

### Asynchronous Communication (RabbitMQ)
- **energy-service** publishes overconsumption events
- **eau-service** consumes these events and takes appropriate actions

## 🚀 Getting Started

### Prerequisites
- Java 17
- Maven 3.x
- MySQL Server
- RabbitMQ (or CloudAMQP account)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/israbelghith/energy_water_management_system_irrigation.git
cd energy_water_management_system_irrigation
```

2. **Start MySQL**
```bash
# Ensure MySQL is running
# Databases will be created automatically: energyDB and eauDB
```

3. **Start Eureka Server**
```bash
cd MSEureka
mvn spring-boot:run
```

4. **Start Energy Service**
```bash
cd energy-service
mvn spring-boot:run
```

5. **Start Water Service**
```bash
cd eau-service
mvn spring-boot:run
```

## 📝 Configuration

Each microservice has its own `application.properties` with:
- Database configuration
- RabbitMQ configuration
- Eureka client configuration
- Service-specific settings

## 🛠️ Technologies

- **Spring Boot 4.0**
- **Spring Cloud** (Netflix Eureka, OpenFeign)
- **Spring Data JPA**
- **Spring AMQP** (RabbitMQ)
- **MySQL**
- **Lombok**
- **Maven**

## 📚 Documentation

Each microservice contains detailed documentation:
- `README.md` - Complete documentation
- API endpoints and usage examples

## 👤 Author

Israa Belghith

## 📄 License

This project is part of a microservices course demonstration.
