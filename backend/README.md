# Enviro365 Withdrawal Notice System — Backend

Junior Developer Technical Assessment (eTalente / Enviro365 Investments, 2026).

Spring Boot backend for a system that lets investors view portfolios, submit
withdrawal notices, and export withdrawal statements as CSV.

## Tech stack

- Java 17
- Spring Boot 3.3.4 (Web, Data JPA, Validation)
- H2 in-memory database
- JUnit 5 + Mockito + AssertJ for unit tests

## Setup

1. Requires JDK 17+ and Maven (or use the included `mvnw` wrapper if present).
2. From the project root:
   ```
   mvn spring-boot:run
   ```
3. The API starts on `http://localhost:8080`.
4. H2 console (to inspect the seeded data live): `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:enviro365`
   - Username: `sa`, Password: *(blank)*
5. Two portfolios are seeded on every startup (see `src/main/resources/data.sql`):
   - Portfolio 1 — Thabo Nkosi, age 68, balance R250,000 (can make retirement withdrawals)
   - Portfolio 2 — Lerato Dube, age 42, balance R90,000 (cannot — demonstrates the age rule)

## Running the tests

```
mvn test
```

Covers all three business rules plus the age>65 boundary and the exact-90%
edge case (`WithdrawalServiceTest`).

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/portfolios` | List all portfolios |
| GET | `/api/portfolios/{id}` | Get one portfolio (investor details + products + balance) |
| POST | `/api/withdrawals` | Submit a withdrawal notice |
| GET | `/api/withdrawals/portfolio/{id}` | Full withdrawal history for a portfolio |
| GET | `/api/withdrawals/portfolio/{id}/export?type=&from=&to=` | Download withdrawal history as CSV, optionally filtered |

### POST /api/withdrawals — request body

```json
{
  "portfolioId": 1,
  "amount": 5000.00,
  "type": "GENERAL"
}
```

`type` is `"GENERAL"` or `"RETIREMENT"`.

### Business rules enforced

1. Retirement withdrawals (`type: "RETIREMENT"`) are only allowed if the investor's age is greater than 65.
2. The withdrawal amount must not exceed the portfolio's current balance.
3. The withdrawal amount must not exceed 90% of the current balance.

A rejected withdrawal returns `400 Bad Request` with a JSON body:
```json
{ "status": 400, "message": "Withdrawal amount (91000.00) exceeds 90% of balance. Maximum allowed: 90000.00", "timestamp": "..." }
```

## Advanced requirements implemented (3+ of 5 required)

- Global exception handling (`GlobalExceptionHandler`)
- DTO layer (separate `dto` package — entities never leave the service layer)
- Input validation (Bean Validation annotations on `WithdrawalRequestDTO`)
- Unit tests (`WithdrawalServiceTest` — 6 tests covering all 3 business rules + edge cases)

## AI usage disclosure

Parts of this solution were built with AI assistance (Claude). Every file was
reviewed and I can explain the reasoning behind each design decision,
including: the entity/DTO separation, why BigDecimal is used for all money
values instead of double, the transactional boundaries around the withdrawal
write path, and why unit tests mock the repositories instead of hitting a
real database.

## Screenshots

_Add screenshots of: the H2 console showing seeded data, a successful POST
via Postman/curl, a rejected withdrawal (400 response), and the running
frontend once complete._
