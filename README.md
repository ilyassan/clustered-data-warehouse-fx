# Bloomberg FX Deals Data Warehouse

[![CI/CD Pipeline](https://github.com/ilyassan/clustered-data-warehouse-fx/workflows/CI%2FCD%20Pipeline/badge.svg)](https://github.com/ilyassan/clustered-data-warehouse-fx/actions)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> **Technologies:** Java 17 • Spring Boot • Spring Data JPA • Spring Validation • PostgreSQL • Liquibase • Maven • Docker • JUnit 5 • Mockito • JaCoCo • MapStruct • Swagger/OpenAPI • GitHub Actions • REST API • Hibernate

A production-ready Spring Boot application for importing, validating, and managing foreign exchange (FX) deal transactions. Built for Bloomberg's data warehouse to analyze FX deals with comprehensive validation, duplicate prevention, and automated deployment.

---

## 🌐 Live Demo

**The application is deployed and running on a production VPS!**

🔗 **Live URL:** [https://ilyassanida.com](https://ilyassanida.com)

**Available Endpoints:**
- **API**: https://ilyassanida.com/api/deals
- **Swagger UI**: https://ilyassanida.com/swagger-ui.html
- **Health Check**: https://ilyassanida.com/actuator/health

**Deployment:** Automated CI/CD pipeline with GitHub Actions deploys on every push to `main` branch.

---

## 🚀 Features

- **RESTful API** - Complete CRUD operations for FX deals
- **Robust Validation** - ISO 4217 currency code validation, field constraints, and business rules
- **Duplicate Prevention** - Automatic detection and rejection of duplicate deal imports
- **No Rollback Policy** - Independent processing of batch imports (failed records don't affect successful ones)
- **Database Migrations** - Version-controlled schema management with Liquibase
- **API Documentation** - Interactive Swagger UI for API exploration and testing
- **Comprehensive Testing** - 35+ unit and integration tests with 85%+ code coverage
- **Containerization** - Docker and Docker Compose for consistent deployments
- **CI/CD Pipeline** - Automated testing, building, and deployment via GitHub Actions
- **Production Ready** - Health checks, logging, error handling, and monitoring

---

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Deployment](#-deployment)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

---

## 🛠 Technology Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.5.7 |
| **Database** | PostgreSQL 16 (Production), H2 (Testing) |
| **Build Tool** | Maven 3.9+ |
| **Database Migration** | Liquibase |
| **API Documentation** | SpringDoc OpenAPI 3 (Swagger) |
| **Object Mapping** | MapStruct 1.6.3 |
| **Testing** | JUnit 5, Mockito, Spring Boot Test, JaCoCo |
| **Containerization** | Docker, Docker Compose |
| **CI/CD** | GitHub Actions |

---

## 🎯 Getting Started

### Prerequisites

- **Java 17** or higher ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Docker & Docker Compose** ([Download](https://www.docker.com/get-started))

### Local Development Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/ilyassan/clustered-data-warehouse-fx.git
cd clustered-data-warehouse-fx
```

#### 2. Run the Application

Choose one of the following methods:

##### **Option A: Using Make (Recommended - Easiest)**

If you have Make installed:

```bash
# Build Docker images and start all services (PostgreSQL + Application)
make docker-start
```

The application will be available at `http://localhost:8080`

**Other useful Make commands:**
```bash
make help             # Show all available commands
make docker-down      # Stop all services
make docker-logs      # View application logs
make docker-restart   # Restart the application
```

**Installing Make:**
- **Windows**: `choco install make` or use Git Bash (comes with Git for Windows)
- **Mac**: Already included with Xcode Command Line Tools
- **Linux**: `sudo apt-get install make`

##### **Option B: Using Docker Compose (Manual)**

If you don't have Make installed:

```bash
# Build and start all services
docker-compose build
docker-compose up -d
```

The application will be available at `http://localhost:8080`

**Useful Docker commands:**
```bash
docker-compose down              # Stop all services
docker-compose logs -f fx-deals-app  # View application logs
docker-compose restart fx-deals-app  # Restart the application
```

##### **Option C: Local Development (Without Docker)**

For local development without Docker:

1. **Start PostgreSQL Database:**
   ```bash
   docker-compose up -d postgres
   ```
   This starts PostgreSQL on port `5455`.

2. **Build the Application:**
   ```bash
   mvn clean install
   ```

3. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```
   Or with Make: `make start`

The application starts on `http://localhost:8080`

#### 3. Verify Installation

Open your browser:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **API Welcome**: http://localhost:8080/

---

## 📚 API Documentation

### Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/deals` | Import a single FX deal |
| `POST` | `/api/deals/batch` | Import multiple FX deals |
| `GET` | `/api/deals` | Retrieve all FX deals |
| `GET` | `/api/deals/{id}` | Retrieve a specific deal by ID |
| `GET` | `/actuator/health` | Application health status |

### Request/Response Examples

#### Import Single Deal

**Request:**
```bash
curl -X POST http://localhost:8080/api/deals \
  -H "Content-Type: application/json" \
  -d '{
    "dealUniqueId": "DEAL-2024-001",
    "fromCurrencyCode": "USD",
    "toCurrencyCode": "EUR",
    "dealTimestamp": "2024-01-15T10:30:00",
    "dealAmount": 1000.50
  }'
```

**Response (201 Created):**
```json
{
  "dealUniqueId": "DEAL-2024-001",
  "fromCurrencyCode": "USD",
  "toCurrencyCode": "EUR",
  "dealTimestamp": "2024-01-15T10:30:00",
  "dealAmount": 1000.50,
  "createdAt": "2024-01-15T10:31:00"
}
```

#### Import Multiple Deals (Batch)

**Request:**
```bash
curl -X POST http://localhost:8080/api/deals/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "dealUniqueId": "DEAL-2024-001",
      "fromCurrencyCode": "USD",
      "toCurrencyCode": "EUR",
      "dealTimestamp": "2024-01-15T10:30:00",
      "dealAmount": 1000.50
    },
    {
      "dealUniqueId": "DEAL-2024-002",
      "fromCurrencyCode": "GBP",
      "toCurrencyCode": "JPY",
      "dealTimestamp": "2024-01-15T11:00:00",
      "dealAmount": 2500.00
    }
  ]'
```

**Response (200 OK):**
```json
{
  "totalRecords": 2,
  "successfulImports": 2,
  "failedImports": 0,
  "successfulDeals": [...],
  "failures": []
}
```

### Interactive Documentation

Visit **[Swagger UI](http://localhost:8080/swagger-ui.html)** for:
- Interactive API testing
- Request/response schemas
- Example payloads
- Error response documentation

---

## 🚢 Deployment

### Docker Deployment (Recommended)

#### Using Docker Compose

```bash
# Build and start all services
docker-compose up --build -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f fx-deals-app

# Stop services
docker-compose down
```

**Access the application:**
- API: http://localhost:8080/api/deals
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Production Deployment (Digital Ocean VPS)

#### Quick Start (30 minutes)

Follow our comprehensive deployment guides:
- **[VPS Deployment Guide](docs/VPS-DEPLOYMENT.md)** - Complete step-by-step guide
- **[Quick Start Deployment](docs/QUICK-START-DEPLOYMENT.md)** - Condensed version

**What you get:**
- Automated CI/CD pipeline
- Zero-downtime deployments
- Auto-deployment on every push to `main`
- Health checks and monitoring
- SSL-ready setup

#### GitHub Actions CI/CD

The project includes automated CI/CD:

1. **On every push/PR**: Tests run automatically
2. **On push to `main`**:
   - Tests run
   - Docker image builds and pushes to Docker Hub
   - Automatically deploys to VPS

**Setup required secrets:**
- `DOCKER_USERNAME` - Docker Hub username
- `DOCKER_PASSWORD` - Docker Hub access token
- `VPS_HOST` - Your VPS IP address
- `VPS_USERNAME` - SSH username (usually `root`)
- `VPS_SSH_KEY` - Private SSH key for authentication

See [.github/workflows/README.md](.github/workflows/README.md) for details.

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage with JaCoCo

The project includes **35+ tests** across multiple layers with **JaCoCo code coverage** reporting:

| Test Type | Count | Coverage |
|-----------|-------|----------|
| **Unit Tests** | 23 tests | Service layer, validators |
| **Integration Tests** | 11 tests | End-to-end API testing |
| **Validation Tests** | 13 tests | Currency validation |

**Code Coverage:** 85%+ line coverage for core business logic (service, validation, controllers)

### Run Specific Test Suites

```bash
# Service layer tests
mvn test -Dtest=DealServiceImplTest

# Integration tests
mvn test -Dtest=DealControllerIntegrationTest

# Validation tests
mvn test -Dtest=CurrencyValidatorTest
```

### Generate Code Coverage Reports

```bash
# Run tests and generate JaCoCo coverage report
mvn clean test

# View HTML report
open target/site/jacoco/index.html
# Or on Windows: start target/site/jacoco/index.html
```

**JaCoCo Reports Generated:**
- **HTML Report**: `target/site/jacoco/index.html` (Interactive coverage report)
- **XML Report**: `target/site/jacoco/jacoco.xml` (For CI/CD integration)
- **CSV Report**: `target/site/jacoco/jacoco.csv` (For data analysis)

**Coverage Details:**
- **Overall Coverage**: 85%+ for business logic
- **Service Layer**: 81% coverage
- **Validation**: 100% coverage
- **Controllers**: 100% coverage
- **Mappers**: 94% coverage
- Build fails if coverage drops below 70% threshold for core packages
- DTOs, entities, and config classes excluded (no business logic)

---

## 📁 Project Structure

```
clustered-data-warehouse-fx/
├── .github/
│   └── workflows/
│       ├── ci-cd.yml                 # CI/CD pipeline configuration
│       └── README.md                 # Workflow documentation
├── docs/
│   ├── VPS-DEPLOYMENT.md             # Complete VPS deployment guide
│   ├── QUICK-START-DEPLOYMENT.md    # Quick deployment guide
│   └── custom-validation-annotation-guide.md
├── scripts/
│   └── vps-setup.sh                  # VPS initialization script
├── src/
│   ├── main/
│   │   ├── java/com/ilyassan/clustereddatawarehousefx/
│   │   │   ├── config/               # Application configuration
│   │   │   ├── controller/           # REST controllers
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── entity/               # JPA entities
│   │   │   ├── exception/            # Exception handling
│   │   │   ├── mapper/               # MapStruct mappers
│   │   │   ├── repository/           # Spring Data repositories
│   │   │   ├── service/              # Business logic
│   │   │   └── validation/           # Custom validators
│   │   └── resources/
│   │       ├── db/changelog/         # Liquibase migrations
│   │       └── application.properties
│   └── test/
│       ├── java/                     # Test classes
│       └── resources/
│           └── application-test.properties
├── docker-compose.yml                # Docker Compose configuration
├── Dockerfile                        # Multi-stage Docker build
├── pom.xml                           # Maven configuration
└── README.md
```

---

## 🔍 Key Design Decisions

### 1. No Rollback Policy

**Requirement:** Successfully imported deals must be saved even if other records in the batch fail.

**Implementation:**
- Batch imports process each deal independently
- Valid deals are saved immediately
- Invalid deals are collected in failure list
- No transaction rollback for failed records

### 2. Duplicate Prevention

**Strategy:**
- Primary key constraint on `deal_unique_id`
- Database-level uniqueness enforcement
- Graceful handling of duplicate attempts
- Clear error messages for duplicates

### 3. Validation Approach

**Custom Currency Validator:**
- `@ValidCurrency` annotation
- Validates against ISO 4217 standard
- Uses Java's `Currency.getAvailableCurrencies()`
- Supports all 180+ currency codes

**Field Validations:**
- Deal Unique ID: Required, max 100 chars
- Currency Codes: Valid ISO 4217
- Deal Amount: Positive, up to 4 decimals
- Deal Timestamp: Valid date-time format

### 4. Database Schema

**Optimized for querying:**
- Indexes on `deal_timestamp`, `from_currency_code`, `to_currency_code`
- Efficient filtering and sorting
- Fast lookup by deal ID (primary key)

---

## 🔒 Security Best Practices

- ✅ Environment-based configuration (no hardcoded credentials)
- ✅ Input validation at multiple layers
- ✅ Parameterized queries (SQL injection prevention)
- ✅ Docker non-root user
- ✅ Health endpoint without sensitive data exposure
- ✅ Firewall configuration (UFW)
- ✅ SSH key authentication (no password access)

---

## 📊 Monitoring and Observability

### Health Endpoints

```bash
# Application health
curl http://localhost:8080/actuator/health

# Response
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### Logging

Structured logging with SLF4J/Logback:
- **INFO**: Successful operations
- **WARN**: Validation failures, duplicates
- **ERROR**: Exceptions, system errors

### Docker Logs

```bash
# View application logs
docker-compose logs -f fx-deals-app

# View database logs
docker-compose logs -f postgres

# View all logs
docker-compose logs -f
```

### Commit Convention

Follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `test:` Test additions/changes
- `refactor:` Code refactoring

<div align="center">

**Made for ProgressSoft**

[Documentation](docs/) · [Report Bug](https://github.com/ilyassan/clustered-data-warehouse-fx/issues) · [Request Feature](https://github.com/ilyassan/clustered-data-warehouse-fx/issues)

</div>
