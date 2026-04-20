# 💸 Costflow API

Backend สำหรับระบบบันทึกรายจ่าย (Expense Tracking)

## 🚀 Tech Stack

* Java 17
* Spring Boot
* PostgreSQL
* Docker + Docker Compose

---

## ⚙️ Run Project (Docker)

```bash
git clone https://github.com/yourname/costflow-api.git
cd costflow-api
docker-compose up -d --build
```

---

## 🌐 API Base URL

```
http://localhost:8081
```

---

## 📌 Example API

### Get all expenses

```bash
GET /expenses
```

### Create expense

```bash
POST /expenses
Content-Type: application/json

{
  "itemId": 1,
  "itemName": "ข้าว",
  "amount": 100,
  "date": "2026-04-20"
}
```

---

## 🗄 Database

* PostgreSQL (Docker)
* Port: 5432
* DB: costflow
* User: postgres
* Password: postgres

---

## 📱 Frontend

React Native App:

👉 https://github.com/ikampfool/expense-app-mobile
