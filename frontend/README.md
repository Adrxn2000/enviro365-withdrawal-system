# Enviro365 Withdrawal Notice System — Frontend

React (Vite) frontend for the Enviro365 assessment. Talks to the Spring Boot
backend for everything — no local mock data.

## Setup

1. Requires Node.js 18+.
2. Install dependencies:
   ```
   npm install
   ```
3. Copy `.env.example` to `.env` if your backend isn't running on the
   default `http://localhost:8080`.
4. Start the dev server (make sure the backend is running first):
   ```
   npm run dev
   ```
5. Open the URL Vite prints (typically `http://localhost:5173`).

## Build for production

```
npm run build
```
Outputs static files to `dist/`.

## Structure

```
src/
├── api/client.js                  All backend requests go through here
├── components/
│   ├── PortfolioSummary.jsx       Investor + balance + products panel
│   ├── WithdrawalForm.jsx         Submit a withdrawal, with client-side validation
│   ├── WithdrawalHistoryTable.jsx History table, filters, CSV download
│   └── StatusBanner.jsx           Shared error/success message bar
├── App.jsx                        Owns state, fetches data, wires everything together
├── App.css                        Component styles (navy/gold design tokens)
└── index.css                      Global reset + design tokens
```

## What each screen does

- **Portfolio panel** — investor name, age, current balance, and products.
- **New withdrawal form** — amount + type (General/Retirement). Client-side
  checks catch an empty/negative amount or one exceeding the visible balance
  before it's even sent; the backend re-validates all three business rules
  regardless, since the API doesn't trust the frontend to enforce them.
- **Withdrawal history** — every past withdrawal, filterable by type and
  date range. The "Download CSV" button applies the same filters server-side.

## Notes

- The portfolio ID is hardcoded to `1` (see `PORTFOLIO_ID` in `App.jsx`) —
  there's no login/investor-selection screen, which is out of scope for
  this assessment.
- AI usage: see the backend README's AI usage disclosure — it applies to
  this frontend too.
