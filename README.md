# Stock Market Simulator 🚀

[![Live Demo](https://img.shields.io/badge/demo-live-green.svg)](https://royal-coin.duckdns.org)
![Docker Image](https://img.shields.io/badge/docker%20image-kashylt/trading--simulator-blue)

Hosted on **Raspberry Pi** at: [https://royal-coin.duckdns.org](https://royal-coin.duckdns.org)

Advanced cryptocurrency trading simulator with portfolio management and real-time analytics.

## Key Features ✨

### Core Functionality
- **User Management**
    - Secure JWT Authentication
    - Email Verification Flow
    - Password Recovery System
    - Admin User Management

- **Trading Engine**
    - Real-time Crypto Prices
    - Virtual Wallet Management
    - Buy/Sell Orders Execution
    - Transaction History Tracking

- **Portfolio Analytics**
    - Performance Metrics
    - Gain/Loss Calculations
    - Asset Distribution Charts
    - Historical Data Analysis

### Advanced Features
- **Alert System**
    - Price Threshold Notifications
    - Email & In-App Alerts
    - Global Admin Announcements

- **Technical Analysis**
    - 10+ Indicators (RSI, MACD, SMA, EMA)
    - Real-time Charts
    - Volatility Metrics
    - Market Trend Predictions

- **Security & Monitoring**
    - Suspicious Activity Detection
    - Admin Audit Logs
    - User Activity Tracking
    - Transaction Anomaly Detection

## Tech Stack 🛠️

| Layer        | Technologies                                                                 |
|--------------|------------------------------------------------------------------------------|
| **Frontend** | React, Axios, React Router, Chart.js, FontAwesome                            |
| **Backend**  | Spring Boot, Spring Security, JWT, JPA/Hibernate, Lombok, Swagger            |
| **Database** | PostgreSQL, Redis Cache                                                     |
| **DevOps**   | Docker, GitHub Actions, Raspberry Pi, Nginx, DuckDNS                        |

## Getting Started 🚦

### Prerequisites
- Docker 20.10+
- Docker Compose 2.20+

### Quick Deployment with Docker

1. **Create config directory**
```bash
mkdir -p ./config
```

2. **Create application.properties (minimal config)
```
# Database
spring.datasource.url=jdbc:postgresql://postgres:5432/trading_simulator
spring.datasource.username=postgres
spring.datasource.password=your_strong_password

# JWT
jwt.secret=your_64_char_secure_secret

# Email (MailDev)
spring.mail.host=maildev
spring.mail.port=1025

# Frontend Configuration
app.frontend.url=http://localhost:8080

```

3. Start services:
```bash
docker compose up -d
```

4. Access services:

- Application: http://localhost:8080

- API Docs: http://localhost:8080/swagger-ui.html

- MailDev: http://localhost:1080

- PostgreSQL: postgres:5432

## Configuration ⚙️
### Docker Compose Template(docker-compose.yml file at root directory)

```
version: '3.8'

services:
  app:
    image: kashylt/trading-simulator:latest
    ports:
      - "8080:8080"
    volumes:
      - ./config/application.properties:/app/config/application.properties
    depends_on:
      - postgres
      - maildev

  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: trading_simulator
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: your_strong_password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  maildev:
    image: maildev/maildev
    ports:
      - "1080:1080"

volumes:
  postgres_data:
```

## API Documentation 📚

### Explore interactive API documentation:

- Local: http://localhost:8080/swagger-ui.html

- Production: https://royal-coin.duckdns.org/swagger-ui.html





