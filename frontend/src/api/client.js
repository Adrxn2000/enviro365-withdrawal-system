// Base URL of the Spring Boot backend. Reads from a Vite env variable so
// the deployed build can point at a different URL than local dev, without
// touching code - see .env.example. Falls back to localhost:8080 (the
// backend's default port) if nothing is set.
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * Every fetch in this file goes through this one function. Centralising
 * it means: one place to parse errors consistently, one place to change
 * headers/base URL later, and every component gets the SAME error shape
 * to display, no matter which endpoint it called.
 */
async function request(path, options = {}) {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (!response.ok) {
    // The backend's GlobalExceptionHandler always returns
    // { status, message, timestamp } JSON on failure - we read that
    // `message` back out so the UI can show the REAL reason (e.g.
    // "exceeds 90% of balance") instead of a generic "Something went wrong".
    let message = `Request failed (${response.status})`;
    try {
      const errorBody = await response.json();
      if (errorBody.message) message = errorBody.message;
    } catch {
      // Response wasn't JSON (e.g. the server didn't respond at all) -
      // fall back to the generic message above rather than throwing here.
    }
    throw new Error(message);
  }

  // CSV export returns text/csv, not JSON - callers that need raw text
  // pass { raw: true } and get the response object back instead.
  if (options.raw) return response;

  // 204 No Content or an empty body would make response.json() throw -
  // guard against that.
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

export function getPortfolio(portfolioId) {
  return request(`/api/portfolios/${portfolioId}`);
}

export function getAllPortfolios() {
  return request('/api/portfolios');
}

export function createWithdrawal({ portfolioId, amount, type }) {
  return request('/api/withdrawals', {
    method: 'POST',
    body: JSON.stringify({ portfolioId, amount, type }),
  });
}

export function getWithdrawalHistory(portfolioId) {
  return request(`/api/withdrawals/portfolio/${portfolioId}`);
}

/**
 * Builds the CSV export URL with whichever filters are set. Used directly
 * as an <a href> rather than fetched via JS, so the browser handles the
 * file download itself (using the Content-Disposition header the backend
 * already sends) instead of us manually creating a Blob and object URL.
 */
export function buildExportUrl(portfolioId, { type, from, to } = {}) {
  const params = new URLSearchParams();
  if (type) params.set('type', type);
  if (from) params.set('from', from);
  if (to) params.set('to', to);
  const query = params.toString();
  return `${BASE_URL}/api/withdrawals/portfolio/${portfolioId}/export${query ? `?${query}` : ''}`;
}
