# Cryptoswarm Services

A Spring Boot microservice for customer registration and management.

## Features

- Customer registration with validation
- PostgreSQL database integration
- Docker Compose setup for local development
- RESTful API with problem detail error responses

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose

## Setup

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd cryptoswarm-services
   ```

2. **Start PostgreSQL and PgAdmin**

   ```bash
   docker compose up -d
   ```

3. **Configure the database**
   - Open web browser, go to `http://localhost:5050`
   - Set a master password for pgAdmin on first connection
   - Add a new server:
     - Name: `cryptoswarm-srv`
     - Connection: `postgres` (connecting from container to container)
     - Username: `cryptoswarm`
     - Password: `Password`
   - Create a database named `customer`

4. **Run the application**
   ```bash
   cd customers-service
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Endpoints

### Register Customer

**POST** `/api/v1/customers`

Request body:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

Success: `200 OK` (no content)

Validation errors: `400 Bad Request` with problem detail response:

```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more validation errors occurred. See the errors property for details.",
  "instance": "/api/v1/customers",
  "timestamp": "2026-04-07T...",
  "errors": {
    "email": "email must be valid"
  }
}
```

## Environment Variables

The `.env` file contains:

- PostgreSQL credentials
- PgAdmin settings

**Important:** Add `.env` to `.gitignore` and never commit sensitive data.

## Development

- Build: `mvn clean package`
- Test: `mvn test`
- Run with custom config: `mvn spring-boot:run -Dspring.config.location=file:./custom.yml`

## Database

The application uses Hibernate with `ddl-auto: update` for schema management.

To reset the database:

```bash
docker compose down -v
docker compose up -d
```

Then recreate the `customer` database in PgAdmin.
