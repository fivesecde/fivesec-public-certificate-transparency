# Fivesec Public Certificate Transparency

Backend and frontend for collecting, indexing, and searching public certificate data.  
Useful for certificate transparency, security monitoring, and research by making certificate information easily accessible and searchable.

Read more about [Certificate Transparency](https://en.wikipedia.org/wiki/Certificate_Transparency)

---

## Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [Prerequisites](#prerequisites)
- [Docker](#run-with-docker)
- [Application Security](#application-security)
- [Database Config](#database-config-postgresql)
- [Configuration](#configuration)
- [API Overview](#api-overview)
- [Authentication](#authentication)
- [Example: Retrieve Data](#example-retrieve-data)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [About Fivesec](#about-fivesec)

---

## Features

- Subscribe to certificate transparency logs.
- Collect events every **3 seconds**.
- Store data in PostgreSQL for indexing and search.
- REST API for easy querying.

---

## Quick Start

### Prerequisites
- Java 21+
- PostgreSQL Database
- Docker (optional, for containerized usage)

### Run with Docker

Build Docker image:

```bash
# Run inside ./backend
docker build -t fivesec-public-certificate-transparency .
```

Run the container:

```bash
docker run -d   -p 8080:8080   -e API_KEY=your-secret-key     # Database
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/ca-db   -e SPRING_DATASOURCE_USERNAME=myuser   -e SPRING_DATASOURCE_PASSWORD=secret     # Certificate Transparency log config
  -e CRT_BASE=https://ct.googleapis.com/logs/us1/argon2025h2   -e CRT_API_KEY=replace-me     fivesec-public-certificate-transparency:latest
```

---

## Application Security

```bash
-e API_KEY=your-secret-key
```

- Used for **API authentication**.
- Clients must send this value in the `x-api-key` header.

---

## Database Config (PostgreSQL)

```bash
-e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/fvsc_dns
-e SPRING_DATASOURCE_USERNAME=myuser
-e SPRING_DATASOURCE_PASSWORD=secret
```

- `SPRING_DATASOURCE_URL` → JDBC connection string.  
  ⚠️ Inside Docker, `localhost` refers to the container itself. Use `host.docker.internal` (Mac/Win) or host IP (Linux).
- `SPRING_DATASOURCE_USERNAME` → Database username.
- `SPRING_DATASOURCE_PASSWORD` → Database password.

---

## Configuration

### Certificate Transparency Log

```bash
-e CRT_BASE=https://ct.googleapis.com/logs/us1/argon2025h2
-e CRT_API_KEY=replace-me
```

- `CRT_BASE` → URL of the CT log stream.
- `CRT_API_KEY` → API key for authenticated access (replace with your own).

---

## Run Locally

Make sure PostgreSQL is running (see `./backend/compose.yaml` for an example).

```bash
./gradlew bootRun
```

API will be available at:  
`http://localhost:8080`

---

## API Overview

```http
GET /api/v1/ca/events
GET /api/v1/ca/events?page=0&size=10
GET /api/v1/ca/events?page=0&size=10&domain=pages.dev
```

---

## Authentication

### API Key (current)
Provide your key via the `x-api-key` header:

```http
GET /api/v1/ca/events
x-api-key: your-secret-key
```

### OAuth2 (planned)
Future releases will support OAuth2/OIDC for enterprise-grade authentication.

---

## Example: Retrieve Data

### Request
```http
GET /api/v1/ca/events?page=0&size=10&domain=pages.dev
Content-Type: application/json
x-api-key: your-secret-key
```

### Response
```json
{
  "data": [
    {
      "id": "97eb90dc-ad94-4653-8029-ee221d264e3d",
      "domain": "plannedparenthoodoforegon.org",
      "subjectDn": "CN=plannedparenthoodoforegon.org",
      "issuerDn": "CN=ZeroSSL ECC Domain Secure Site CA,O=ZeroSSL,C=AT",
      "notBefore": "2025-09-29T00:00:00Z",
      "notAfter": "2025-12-28T23:59:59Z",
      "serialHex": "332edae3635b3b9e45f3951d43701268"
    }
  ],
  "slicing": {
    "page": 0,
    "size": 1,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## Roadmap
- [ ] OAuth2 / OIDC Authentication
- [ ] Multi-log aggregation (multiple CT logs at once)
---

## Contributing
We ❤️ contributions!  
See our [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## License
[MIT License](./LICENSE) – free for personal and commercial use.

---

## About Fivesec
Fivesec builds open-source and enterprise-ready **security, AI & infrastructure tools**.  
🌐 Visit us: [Fivesec](https://fivesec.de/de/fivesec-de/)  
