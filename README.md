# 🏆 Reward System API

This Spring Boot application calculates customer reward points based on transaction history. It features an internal reward logic system and supports dynamic multipliers fetched from an external service.

## 📦 Features

- Retrieve all customer rewards
- Calculate reward points per customer for a custom date range
- Integrate with an external service to apply monthly multipliers
- Logs API calls for traceability
- Modular design with Spring Boot + WebClient

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Spring Boot 3.x
- MongoDB (for customer and transaction persistence)
- External multiplier API (mock or real)

### Tech Stack

- Spring Boot (RESTful API)
- Spring Boot (RESTful API)
- Lombok (boilerplate reduction)
- SLF4J Logging
- Mysql DB , Hibernate


### Project Structure

com.kubertech.rewardsystem
├── controller/             # REST endpoints
├── service/                # Business logic
├── model/                  # Domain models (Customer, Transaction, RewardSummary)
├── repository/             # Database access layer
└── config/                 # WebClient config

### Run the App

```bash
mvn spring-boot:run