# Smart Grocery Expiry Management System

This project contains a Java Spring Boot backend and a React + Tailwind frontend for grocery expiry tracking.

## Project structure

- `backend/` - Spring Boot + Maven backend
- `frontend/` - React + Vite + Tailwind dashboard
- `database/` - PostgreSQL schema and Prisma schema

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+
- PostgreSQL 14+
- Docker + Docker Compose (optional)

## Backend

```bash
cd backend
mvn spring-boot:run
```

Configure environment variables using `.env.example` or `application.yml`.

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Then open http://localhost:5173

## Docker Compose

```bash
docker-compose up --build
```

## Features included

- OCR upload flow for grocery bills
- Rule-based category matching
- Expiry status calculation
- Dashboard overview with inventory table and upload panel

## Notes

The current implementation is a working skeleton and is designed to be extended with full JPA entities, JWT auth, and PostgreSQL persistence.
