# 🥔 Potato App

**Potato** is an application designed to serve both web and mobile users.  
The backend is developed with **Spring Boot** and provides **RESTful APIs**, offering a robust and maintainable foundation for client applications.  
For the frontend, **ReactJS** powers the web interface, delivering an interactive and responsive user experience across platforms.

This project demonstrates how to design and implement a **scalable monolithic system** using well-structured layers — separating concerns between the user interface, business logic, and data access — while enabling multiple client platforms to interact seamlessly via RESTful endpoints.

It is mainly designed by the Spring ecosystem, leveraging popular libraries that have been widely adopted by the community for years, combined with modern frontend technologies to provide a responsive, maintainable solution for real-world **food delivery** scenarios.

---

## 📚 Table of Contents

1. [Technologies Used](#technologies-used)  
2. [Architecture Overview](#architecture-overview)  
3. [Project Structure](#project-structure)  
4. [Setup Instructions](#setup-instructions)  
5. [Running with Docker](#running-with-docker)   

---

## ⚙️ Technologies Used

### 🧠 Backend
- **Spring Boot 3+**
- **Spring Data JPA**
- **Spring Security (JWT Authentication)**
- **MySQL 8+**
- **Docker & Docker Compose**
- **Swagger / OpenAPI Documentation**

### 🎨 Frontend
- **ReactJS (Vite or Create React App)**
- **Axios for API calls**
- **TailwindCSS / MUI for UI**
- **React Router DOM for navigation**

---

## 🏗️ Architecture Overview

This application follows a **modular monolith**:


REST APIs act as the communication bridge between the frontend and backend.

---

## 📁 Project Structure

```plaintext
Potato-App/
├── potato-api/
│   ├── src/
│   │   ├── main/java/com/ktpm/potatoapi
│   │   │   ├── cart/
│   │   │   ├── category/
│   │   │   ├── cloudinary/
│   │   │   ├── common/
│   │   │   ├── cusinetype/
│   │   │   ├── feedback/
│   │   │   ├── mail/
│   │   │   ├── menu/
│   │   │   ├── merchant/
│   │   │   ├── option/
│   │   │   ├── order/
│   │   │   ├── user/
│   │   │   └── PotatoApiApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       └── application.yml
│   ├── pom.xml
│   └── Backend.dockerfile
│
├── customer_fe/
│   ├── public/
│   ├── src/
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.js
│   └── Frontend.dockerfile
│
├── docker-compose.yml
└── README.md
```
---
## 4. Setup Instructions

Follow these steps to set up the project locally.

### 🧩 Prerequisites
Before running the app, make sure you have installed:
- **Java 17+**
- **Node.js 18+**
- **MySQL 8+**
- **Maven 3.8+**
- *(Optional)* **Docker Desktop**

---

### 🗄️ Step 1 — Setup Database

Create a MySQL database named `potato_db`:

```sql
mysql -u root -p -e "CREATE DATABASE potato_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Then, update database credentials inside:

**backend/src/main/resources/application.yml**:
```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/potato_db
    username: root
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```
### ⚙️ Step 2 — Run Backend (Spring Boot)
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```
Backend will start at:

👉 <http://localhost:8080/potato-api>

Swagger UI is available at:

👉 <http://localhost:8080/potato-api/swagger-ui/index.html#>


### 💻 Step 3 — Run Frontend (ReactJS)
```bash
cd frontend
npm install
npm run dev
```
Frontend will start at:

👉 <http://localhost:3000>


## Running with Docker
Easily deploy the full stack using Docker.

### 🧱 Step 1 — Build and Run
```bash
docker compose up --build
```
### 🌐 Step 2 — Access Services
* Frontend: <http://localhost:3000>

* Backend: <http://localhost:8080/potato-api>

* MySQL: Port 3306

### 🧹 Step 3 — Stop Containers
```bash
docker compose down
```
> 💡 Tip: Add -v after down to also remove volumes and reset database data.

