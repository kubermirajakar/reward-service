# 🏆 Reward System API

A Spring Boot application that calculates customer reward points based on transaction history. It aggregates monthly and total points, with full write operations for customers and transactions.

---

## 📦 Features

- Calculate reward points based on business rules
- Retrieve reward summaries for all customers or per customer with date filters
- Create customers & transactions
- Logs API calls for traceability
- Modular and scalable design using Spring Boot

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven 3.8+
- Spring Boot 3.x
- Hibernate & MySQL (for customer and transaction persistence)

### 🧱 Tech Stack

- Spring Boot (RESTful API)
- Lombok (Builder pattern & boilerplate removal)
- SLF4J Logging
- Hibernate & MySQL (Persistence layer)

### 📁 Project Structure

```
com.kubertech.rewardsystem

├── config/                 # Security config (if any)
├── controller/             # REST endpoints
├── exception/              # Golbal Exceptions
├── model/                  # Domain models (Customer, Transaction, RewardSummary , CustomerBasicDTO , MonthlyPointDTO)
├── repository/             # Database access layer
├── service/                # Business logic
└── utility/                # Reward Points utility
```

---

## 🔄 API Endpoints

### 👤 Customer Endpoints

#### 1. Create a Customer
- `POST /api/rewards/customers`

**Request:**
```json
{
  "name": "Alice"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Alice"
}
```

---

### 💸 Transaction Endpoints

#### 2. Create Transaction
- `POST /api/rewards/transactions`

**Request:**
```json
{
  "amount": 120.0,
  "transactionDate": "2025-07-10",
  "customer": {
    "id": "1"
  }
}
```

**Response:**
```json
{
  "id": 1001,
  "amount": 120.0,
  "transactionDate": "2025-07-10"
}
```

---

### 🧾 Reward Summary Endpoints

#### 3. Fetch All Customer Reward Summaries
- `GET /api/rewards`

**Response:**
```json
[
  {
    "customerId": 1,
    "customerName": "Krishna",
    "transactions": [
      {
        "id": 3,
        "amount": 90.0,
        "transactionDate": "2024-04-25"
      },
      {
        "id": 2,
        "amount": 150.0,
        "transactionDate": "2024-05-25"
      },
      {
        "id": 1,
        "amount": 132.0,
        "transactionDate": "2024-06-25"
      }
    ],
    "monthlyPoints": [
      {
        "year": 2024,
        "month": "April",
        "points": 40
      },
      {
        "year": 2024,
        "month": "May",
        "points": 150
      },
      {
        "year": 2024,
        "month": "June",
        "points": 114
      }
    ],
    "totalPoints": 304
  },
  {
    "customerId": 2,
    "customerName": "Kuber",
    "transactions": [
      {
        "id": 4,
        "amount": 80.0,
        "transactionDate": "2024-04-25"
      },
      {
        "id": 5,
        "amount": 90.0,
        "transactionDate": "2024-05-25"
      },
      {
        "id": 6,
        "amount": 100.0,
        "transactionDate": "2024-06-25"
      }
    ],
    "monthlyPoints": [
      {
        "year": 2024,
        "month": "April",
        "points": 30
      },
      {
        "year": 2024,
        "month": "May",
        "points": 40
      },
      {
        "year": 2024,
        "month": "June",
        "points": 50
      }
    ],
    "totalPoints": 120
  },
  {
    "customerId": 3,
    "customerName": "khot",
    "transactions": [
      {
        "id": 7,
        "amount": 50.0,
        "transactionDate": "2024-04-25"
      },
      {
        "id": 8,
        "amount": 60.0,
        "transactionDate": "2024-05-25"
      },
      {
        "id": 9,
        "amount": 70.0,
        "transactionDate": "2024-06-25"
      }
    ],
    "monthlyPoints": [
      {
        "year": 2024,
        "month": "April",
        "points": 0
      },
      {
        "year": 2024,
        "month": "May",
        "points": 10
      },
      {
        "year": 2024,
        "month": "June",
        "points": 20
      }
    ],
    "totalPoints": 30
  }
]
```

#### 4. Fetch Reward Summary for a Specific Customer in a Date Range
- `GET /api/rewards/{customerId}?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`

**Response:**
```json
{
  "customerId": 1,
  "customerName": "Krishna",
  "transactions": [
    {
      "id": 3,
      "amount": 90.0,
      "transactionDate": "2024-04-25"
    },
    {
      "id": 2,
      "amount": 150.0,
      "transactionDate": "2024-05-25"
    },
    {
      "id": 1,
      "amount": 132.0,
      "transactionDate": "2024-06-25"
    }
  ],
  "monthlyPoints": [
    {
      "year": 2024,
      "month": "April",
      "points": 40
    },
    {
      "year": 2024,
      "month": "May",
      "points": 150
    },
    {
      "year": 2024,
      "month": "June",
      "points": 114
    }
  ],
  "totalPoints": 304
}
```

---



## ▶️ Run the App

## 🚀 Getting Started

###  Clone the Repository

```bash
git clone https://github.com/kubermirajakar/reward-service.git
cd kubermirajakar
```

###  Build the Project
```bash
mvn clean install
```

###  Run the Application

```bash
mvn spring-boot:run
```
