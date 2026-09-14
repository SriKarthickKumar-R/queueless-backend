# QueueLess — Backend

REST API backend for QueueLess, a smart queue and appointment management system that helps hospitals reduce waiting time and improve patient flow. Built with Java and Spring Boot, backed by MySQL. Pairs with the [QueueLess frontend](https://github.com/SriKarthickKumar-R/queueless-frontend), a React client.

**Live API:** deployed on Render
**Frontend repo:** https://github.com/SriKarthickKumar-R/queueless-frontend
**Frontend demo:** https://queueless-frontend-psi.vercel.app/

## What it does

Exposes a REST API that powers a digital hospital queue and appointment system:

- Appointment booking and management
- Live queue position tracking
- User management with role-based access across patients, doctors, and administrators
- Relational data persistence in MySQL via Spring Data JPA

## Tech stack

- **Java**
- **Spring Boot**
- **Spring Data JPA** — ORM and repository layer
- **MySQL** — relational database
- **Maven** — build and dependency management
- **Docker** — containerised for deployment
- Deployed on **Render**

## Architecture

This service exposes REST endpoints consumed by the [QueueLess React frontend](https://github.com/SriKarthickKumar-R/queueless-frontend). Data is modelled relationally across patients, doctors, appointments, and queue entries, persisted in MySQL and accessed through Spring Data JPA repositories. CORS is configured to accept requests from the deployed frontend origin.

## Getting started

### Prerequisites

- JDK 17+
- Maven (or use the included `mvnw` wrapper)
- A running MySQL instance

### Run locally

```bash
# Clone the repo
git clone https://github.com/SriKarthickKumar-R/queueless-backend.git
cd queueless-backend

# Configure your database connection in
# src/main/resources/application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/queueless
# spring.datasource.username=your_username
# spring.datasource.password=your_password

# Run with the Maven wrapper
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080` by default.

### Run with Docker

```bash
docker build -t queueless-backend .
docker run -p 8080:8080 queueless-backend
```

## Related repository

- Frontend (React + Vite): https://github.com/SriKarthickKumar-R/queueless-frontend

## Author

**Sri Karthick Kumar R**
[GitHub](https://github.com/SriKarthickKumar-R) · [LinkedIn](https://linkedin.com/in/srikarthickkumar-r)
