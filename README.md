# Mon Coeur - Java Backend

Mon Coeur is a modular microservices-based backend system for an e-commerce bakery store. It is implemented in Java with Spring Boot and uses technologies like Kafka, Keycloak, and MinIO to handle various services such as analytics, authentication, order management, media storage, and API gateway routing.

## 🧱 Project Structure

This backend is divided into several independent microservices:

Java-MonCoeur
- analytic_service/ # Handles data analytics and metrics aggregation
- api-gateway/ # Routes requests to backend services and enforces authentication
- email-service/ # Sends email notifications
- identity-service/ # Manages users and roles (integrated with Keycloak)
- inventory-service/ # Manages product stock and availability
- media-service/ # Manages file uploads/downloads using MinIO
- notification-service/ # Sends system-wide notifications
- order-service/ # Handles orders, carts, and checkout logic
- payment_service/ # Processes and verifies payments
- product-service/ # Manages products and categories
- search-service/ # Provides Elasticsearch-based product search
- workspace-service/ # Handles workspace and user grouping logic

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose
- Keycloak (for identity & access management)
- MinIO (for object storage)
- Kafka & Zookeeper
- MySQL or PostgreSQL (based on service configuration)

### Running the Project

> **Note:** Each service has its own `pom.xml` file and runs independently.

You can run services individually using:

docker-compose up --build
Environment Variables
Some services depend on the following environment variables or configuration files:

application.yml – typically contains DB, Kafka, and Keycloak settings.

Kafka brokers and topics should be consistent across producer/consumer services.

Keycloak configuration must include matching realm, client, and scopes.

🔐 Authentication
Identity and authorization are managed using Keycloak.

The API Gateway introspects tokens and validates them before routing.

Each service can also validate JWT tokens directly using a JwtDecoder.

📦 Technologies Used
Spring Boot for service development

Spring Security + OAuth2 for authentication

Keycloak as an identity provider

Kafka for inter-service communication

Elasticsearch for product search

MinIO for media storage

MapStruct for DTO mapping

PostgreSQL/MySQL for data persistence

Lombok, JUnit, and Mockito for cleaner code and testing

🛠️ Build & Test
To build a service:
mvn clean install
To run tests:
mvn test
📈 Analytics
The analytic_service consumes Kafka events and periodically aggregates data (e.g., daily views, orders) using scheduled tasks.

📬 Notification
email-service and notification-service handle email and system notifications.

Supports asynchronous communication using Kafka.

🧪 API Testing
Postman or Swagger UI (enabled by SpringFox/OpenAPI config if present) can be used to test endpoints.

📝 License
This project is under the MIT License.

✍️ Author
Developed by Trần Nam Hải and contributors.
