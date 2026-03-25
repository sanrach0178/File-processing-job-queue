# Job Queue System - Asynchronous CSV Processing 🚀

A high-performance, asynchronous job queue system built with **Spring Boot 3**, **Redis**, and **PostgreSQL**. Designed to handle large CSV file uploads and process data in the background with full observability and professional error handling.

---

## 🏗️ Architecture & Core Features

- **Asynchronous Processing**: Upload files and immediately receive a `Job ID`. The system processes data in the background using a dedicated `JobWorker`.
- **Redis-Backed Job Queue**: Uses Redis as a robust message broker to manage job distribution and status updates.
- **Relational Data Management**: Stores job metadata and processed CSV records in PostgreSQL for long-term persistence.
- **Global Error Handling**: Centralized exception management providing structured JSON error responses.
- **Automatic API Documentation**: Swagger UI integration for interactive API exploration and testing.
- **Input Validation**: JSR-303/Jakarta validation for file type, size, and content integrity.
- **Containerized Deployment**: Ready-to-run with Docker and Docker Compose.

---

## 🛠️ Technology Stack

- **Java 21 (LTS)**
- **Spring Boot 3.3.4**
- **Persistence**: PostgreSQL (SQL), Redis (Cache/Queue)
- **Monitoring**: SLF4J + Logback
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Testing**: JUnit 5, Mockito
- **DevOps**: Docker, Docker Compose, Maven

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Maven (optional, if running locally)

### One-Command Setup (Docker)
1. Clone the repository:
   ```bash
   git clone https://github.com/sanrach0178/File-processing-job-queue.git
   cd File-processing-job-queue
   ```
2. Spin up the entire stack:
   ```bash
   docker-compose up --build
   ```
   The app will be available at `http://localhost:8080`.

---

## 📖 API Usage

### 1. Submit a Job
Upload a CSV file (e.g., `name, age, city`) to the system.
- **Endpoint**: `POST /jobs`
- **Payload**: `multipart/form-data` with a `file` field.

### 2. Check Job Status
Monitor the progress of your upload.
- **Endpoint**: `GET /jobs/{id}`

### 3. Retrieve CSV Details
Fetch all parsed records for a specific job once completed.
- **Endpoint**: `GET /jobs/{id}/details`

### 4. Interactive Documentation
Explore the full API spec and test endpoints:
`http://localhost:8080/swagger-ui/index.html`

---

## 🛡️ Professional Standards
This project follows industry best practices:
- **Clean Code**: SOLID principles and clear package structure.
- **Observability**: Production-ready logging for lifecycle tracking.
- **Hardened Security**: Input type and size validation to prevent malicious uploads.
- **Unit Testing**: 100% coverage on critical service logic.

---

## 🤝 Contributing
Feel free to fork this project and submit PRs for new features or improvements!

---

*Built for professional backend development excellence.*
