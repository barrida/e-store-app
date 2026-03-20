# E-Store Application

A high-traffic e-commerce platform built with a reactive microservices architecture using Java, Spring Boot, and Project Reactor.

## Overview

E-Store is a production-ready e-commerce platform designed for scalability and resilience. It follows a microservices architecture where each service is independently deployable, uses reactive programming for non-blocking I/O, and is backed by PostgreSQL databases.

## Technology Stack

| Component            | Technology                              |
|----------------------|-----------------------------------------|
| Language             | Java 17+                                |
| Framework            | Spring Boot 3.x                         |
| Reactive Runtime     | Project Reactor (WebFlux)               |
| Build Tool           | Maven (monorepo with parent POM)        |
| Database             | PostgreSQL (R2DBC for reactive access)  |
| Orchestration        | Kubernetes (on-premises)                |
| Containerization     | Docker                                  |
| Testing              | JUnit 5, Mockito, Testcontainers        |

## Project Structure

```
e-store-app/
├── pom.xml                          # Parent POM – dependency & plugin management
├── shared-libs/                     # Common utilities shared by all services
│   └── src/main/java/com/estore/shared/
│       ├── dto/                     # Shared DTOs (e.g. ApiResponse)
│       ├── exception/               # Base exception classes
│       ├── model/                   # Shared domain models
│       └── util/                    # Utility classes
├── product-catalog/                 # Product Catalog Service (port 8081)
├── user-management/                 # User Management Service  (port 8082)
├── order-management/                # Order Management Service (port 8083)
├── payment-processing/              # Payment Processing Service (port 8084)
├── notification-service/            # Notification Service (port 8085)
├── docs/
│   ├── architecture/                # System architecture diagrams and decisions
│   ├── database-schemas/            # Database schema definitions and ERDs
│   ├── api-documentation/           # OpenAPI/Swagger specs and API guides
│   ├── deployment/                  # Deployment guides and runbooks
│   ├── performance-reports/         # Load, stress, and spike test results
│   └── security-reports/            # Security scan results and hardening notes
└── config/
    ├── kubernetes/                  # Kubernetes manifests (Deployments, Services, etc.)
    ├── docker/                      # Dockerfiles and docker-compose files
    └── ci-cd/                       # CI/CD pipeline configurations
```

## Microservices

| Service              | Artifact ID            | Port | Description                                    |
|----------------------|------------------------|------|------------------------------------------------|
| Product Catalog      | `product-catalog`      | 8081 | Product listings, categories, inventory        |
| User Management      | `user-management`      | 8082 | User accounts, profiles, authentication        |
| Order Management     | `order-management`     | 8083 | Order lifecycle and fulfillment                |
| Payment Processing   | `payment-processing`   | 8084 | Payment transactions and refunds               |
| Notification Service | `notification-service` | 8085 | Email, SMS, and push notifications             |
| Shared Libraries     | `shared-libs`          | —    | Common utilities, DTOs, and exception handling |

## Prerequisites

- **Java 17+** – [Download](https://adoptium.net/)
- **Maven 3.8+** – [Download](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** – [Download](https://docs.docker.com/get-docker/)
- **PostgreSQL 15+** – or use the provided Docker Compose file
- **kubectl** (for Kubernetes deployments) – [Download](https://kubernetes.io/docs/tasks/tools/)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/barrida/e-store-app.git
cd e-store-app
```

### 2. Build All Modules

```bash
mvn clean install -DskipTests
```

### 3. Start Infrastructure (PostgreSQL via Docker)

```bash
# Coming soon: docker-compose file in config/docker/
docker-compose -f config/docker/docker-compose.yml up -d
```

### 4. Run a Specific Service

```bash
# Example: run the product-catalog service
cd product-catalog
mvn spring-boot:run
```

### 5. Run All Tests

```bash
mvn test
```

## Database Configuration

Each microservice uses its own PostgreSQL database:

| Service              | Database Name           | Default Port |
|----------------------|-------------------------|--------------|
| Product Catalog      | `estore_products`       | 5432         |
| User Management      | `estore_users`          | 5432         |
| Order Management     | `estore_orders`         | 5432         |
| Payment Processing   | `estore_payments`       | 5432         |
| Notification Service | `estore_notifications`  | 5432         |

Override database credentials with environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

## Service Ports

| Service              | HTTP Port |
|----------------------|-----------|
| Product Catalog      | 8081      |
| User Management      | 8082      |
| Order Management     | 8083      |
| Payment Processing   | 8084      |
| Notification Service | 8085      |

## Project Roadmap

- **Phase 1** ✅ – Monorepo scaffolding (this PR)
- **Phase 2** – Containerization and Kubernetes orchestration
- **Phase 3** – PostgreSQL schemas and R2DBC repository implementations
- **Phase 4** – Authentication, authorization, and HTTPS
- **Phase 5** – Performance testing (JMeter / k6)
- **Phase 6** – CI/CD pipeline and monitoring (Prometheus / Grafana)

## Contributing

Please read the documentation in `docs/` before contributing. Each microservice should:
1. Follow the reactive programming model (return `Mono<T>` or `Flux<T>`)
2. Include unit tests and integration tests using Testcontainers
3. Expose `/actuator/health` and `/actuator/info` endpoints

## License

This project is for educational and portfolio purposes.
