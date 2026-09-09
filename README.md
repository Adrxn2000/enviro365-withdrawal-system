# Enviro365 Investments Withdrawal Notice System

Junior Developer Technical Assessment, submitted to eTalente for Enviro365
Investments (2026).

## Background

Enviro365 Investments is automating its withdrawal notice process to
eliminate manual errors, improve efficiency, and deliver a better investor
experience. This system lets investors view their portfolio, submit
withdrawal requests, and download their withdrawal history as a CSV
statement.

## Tech stack

- **Backend:** Java 17, Spring Boot 3.3.4 (Web, Data JPA, Validation), H2
  in-memory database, JUnit 5 + Mockito + AssertJ
- **Frontend:** React 19 (Vite), plain CSS with design tokens — no UI
  framework dependency

## Repository structure

enviro365-withdrawal-system/
├── backend/ Spring Boot REST API — see backend/README.md
└── frontend/ React dashboard — see frontend/README.md


## Features

- View any seeded investor's portfolio via a switcher — no code editing
  needed to demo different investors (e.g. one under 65, one over).
- Submit a withdrawal with real-time validation feedback.
- Browse withdrawal history, filterable by type and date range.
- Export withdrawal history as a CSV statement.

## Business rules implemented

1. Retirement withdrawals are only allowed if the investor is older than 65.
2. A withdrawal must not exceed the portfolio's current balance.
3. A withdrawal must not exceed 90% of the current balance.

All three are enforced server-side (`WithdrawalService`), with matching
client-side checks in the React form for faster feedback.

## Quick start

1. **Backend** — from `backend/`:

mvn spring-boot:run

   Starts on http://localhost:8080. Seeded portfolios are created
   automatically on every startup (see backend/README.md for details).

2. **Frontend** — from `frontend/`:

npm install
npm run dev

   Starts on http://localhost:5173 and connects to the backend above.

## Advanced requirements (3+ of 5 required — all 5 implemented)

- Global exception handling
- DTO layer
- Input validation
- Unit tests
- UI validation

## AI usage disclosure

Parts of this solution were built with AI assistance (Claude). Every file
was reviewed, and the reasoning behind each design decision — entity/DTO
separation, BigDecimal for money, transactional boundaries, the mocking
strategy in the unit tests — can be explained on request. See
backend/README.md for the full disclosure.

## Screenshots

See `/screenshots` for the H2 console with seeded data, a successful and
a rejected withdrawal request, and the running frontend.

## Documentation

- [Backend README](./backend/README.md) — full API reference, setup, business rules
- [Frontend README](./frontend/README.md) — component structure, setup
