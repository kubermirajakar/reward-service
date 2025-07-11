# 🏆 Reward System API

A Spring Boot application that calculates customer reward points based on transaction history. It aggregates monthly and total points, with full CRUD operations for customers and transactions.

---

## 📦 Features

- Calculate reward points based on business rules
- Retrieve reward summaries for all customers or per customer with date filters
- Create, read, update, and delete customers & transactions
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
├── controller/             # REST endpoints
├── service/                # Business logic
├── model/                  # Domain models (Customer, Transaction, RewardSummary)
├── repository/             # Database access layer
└── config/                 # Security config (if any)
```

---

## 🔄 API Endpoints

### 🧾 Reward Summary Endpoints

#### 1. Fetch All Customer Reward Summaries
- `GET /api/rewards`

**Response:**
```json
[
  {
    "customerId": "1",
    "customerName": "Alice",
    "monthlyPoints": {
      "2025-07": 120
    },
    "totalPoints": 120,
    "transactions": [
      {
        "id": 1001,
        "amount": 100.0,
        "transactionDate": "2025-07-10"
      }
    ]
  }
]
```

#### 2. Fetch Reward Summary for a Specific Customer in a Date Range
- `GET /api/rewards/{customerId}?start=YYYY-MM-DD&end=YYYY-MM-DD`

**Response:**
```json
{
  "customerId": "1",
  "customerName": "Alice",
  "monthlyPoints": {
    "2025-06": 90
  },
  "totalPoints": 90,
  "transactions": [
    {
      "id": 1002,
      "amount": 90.0,
      "transactionDate": "2025-06-15"
    }
  ]
}
```

---

### 👤 Customer Endpoints

#### 3. Create a Customer
- `POST /api/rewards/customers`

**Request:**
```json
{
  "id": "1",
  "name": "Alice"
}
```

**Response:**
```json
{
  "id": "1",
  "name": "Alice"
}
```

#### 4. Fetch All Customers
- `GET /api/rewards/customers`

**Response:**
```json
[
  {
    "id": "1",
    "name": "Alice"
  }
]
```

#### 5. Fetch Customer by ID
- `GET /api/rewards/customers/{id}`

**Response:**
```json
{
  "id": "1",
  "name": "Alice"
}
```

#### 6. Update Customer
- `PUT /api/rewards/customers/{id}`

**Request:**
```json
{
  "id": "1",
  "name": "Alice Updated"
}
```

**Response:**
```json
{
  "id": "1",
  "name": "Alice Updated"
}
```

#### 7. Delete Customer
- `DELETE /api/rewards/customers/{id}`

**Response:**
```json
"Customer has been successfully deleted."
```

---

### 💸 Transaction Endpoints

#### 8. Create Transaction
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

#### 9. Fetch All Transactions
- `GET /api/rewards/transactions`

**Response:**
```json
[
  {
    "id": 1001,
    "amount": 120.0,
    "transactionDate": "2025-07-10"
  }
]
```

#### 10. Fetch Transaction by ID
- `GET /api/rewards/transactions/{id}`

**Response:**
```json
{
  "id": 1001,
  "amount": 120.0,
  "transactionDate": "2025-07-10"
}
```

#### 11. Update Transaction
- `PUT /api/rewards/transactions/{id}`

**Request:**
```json
{
  "id": 1001,
  "amount": 150.0,
  "transactionDate": "2025-07-11",
  "customer": {
    "id": "1"
  }
}
```

**Response:**
```json
{
  "id": 1001,
  "amount": 150.0,
  "transactionDate": "2025-07-11"
}
```

#### 12. Delete Transaction
- `DELETE /api/rewards/transactions/{id}`

**Response:**
```json
"Transaction has been deleted successfully."
```

---

## ▶️ Run the App

```bash
mvn spring-boot:run
```
