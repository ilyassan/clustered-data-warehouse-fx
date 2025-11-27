.PHONY: help build test run start clean docker-build docker-start docker-up docker-down docker-logs coverage

help:
	@echo "Bloomberg FX Deals Data Warehouse - Available Commands:"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-start   - Build and start all services with Docker Compose"
	@echo "  make docker-build   - Build Docker image"
	@echo "  make docker-up      - Start all services with Docker Compose"
	@echo "  make docker-down    - Stop all services"
	@echo "  make docker-logs    - View application logs"
	@echo "  make docker-restart - Restart the application container"
	@echo ""
	@echo "Development:"
	@echo "  make build          - Build the project with Maven"
	@echo "  make test           - Run all tests"
	@echo "  make coverage       - Run tests and generate JaCoCo coverage report"
	@echo "  make run            - Run the application locally"
	@echo "  make start          - Build and run the application"
	@echo "  make clean          - Clean build artifacts"
	@echo ""
	@echo "Database:"
	@echo "  make db-up          - Start only PostgreSQL database"
	@echo "  make db-down        - Stop PostgreSQL database"
	@echo "  make db-logs        - View database logs"
	@echo ""

# Build the project
build:
	@echo "Building the project..."
	mvn clean install -DskipTests

# Run all tests
test:
	@echo "Running tests..."
	mvn test

# Run tests and generate coverage report
coverage:
	@echo "Running tests with coverage..."
	mvn clean test
	@echo "Coverage report generated at: target/site/jacoco/index.html"

# Run the application
run:
	@echo "Starting the application..."
	mvn spring-boot:run

# Build and run the application
start: build
	@echo "Starting the application..."
	mvn spring-boot:run

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	mvn clean

# Build Docker image
docker-build:
	@echo "Building Docker image..."
	docker-compose build

# Build and start all services
docker-start: docker-build
	@echo "Starting all services..."
	docker-compose up -d
	@echo "Application will be available at: http://localhost:8080"
	@echo "Swagger UI: http://localhost:8080/swagger-ui.html"

# Start all services
docker-up:
	@echo "Starting all services..."
	docker-compose up -d
	@echo "Application will be available at: http://localhost:8080"
	@echo "Swagger UI: http://localhost:8080/swagger-ui.html"

# Stop all services
docker-down:
	@echo "Stopping all services..."
	docker-compose down

# View application logs
docker-logs:
	@echo "Viewing application logs (Ctrl+C to exit)..."
	docker-compose logs -f fx-deals-app

# Restart application container
docker-restart:
	@echo "Restarting application container..."
	docker-compose restart fx-deals-app

# Start only database
db-up:
	@echo "Starting PostgreSQL database..."
	docker-compose up -d postgres
	@echo "Database available at: localhost:5455"

# Stop database
db-down:
	@echo "Stopping PostgreSQL database..."
	docker-compose stop postgres

# View database logs
db-logs:
	@echo "Viewing database logs (Ctrl+C to exit)..."
	docker-compose logs -f postgres
