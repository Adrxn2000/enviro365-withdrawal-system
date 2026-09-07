import { useEffect, useState, useCallback } from 'react';
import './App.css';
import { getPortfolio, getWithdrawalHistory, createWithdrawal } from './api/client';
import PortfolioSummary from './components/PortfolioSummary';
import WithdrawalForm from './components/WithdrawalForm';
import WithdrawalHistoryTable from './components/WithdrawalHistoryTable';
import StatusBanner from './components/StatusBanner';

// Hardcoded to the first seeded portfolio (Thabo Nkosi, age 68 - see the
// backend's data.sql). A real multi-investor app would get this from a
// login/selection screen; out of scope for this assessment, so it's a
// single constant instead of over-building a feature nobody asked for.
const PORTFOLIO_ID = 1;

/**
 * App.jsx is the "container" component: it owns all the state (portfolio,
 * history, loading, errors) and all the API calls, then passes plain data
 * and callback functions down to the presentational components above.
 * This split - "smart" container vs "dumb" presentational components -
 * means PortfolioSummary/WithdrawalForm/WithdrawalHistoryTable can be
 * understood, reused, or tested without knowing anything about fetch()
 * or API URLs at all.
 */
export default function App() {
  const [portfolio, setPortfolio] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // useCallback keeps this function's identity stable across re-renders,
  // so it's safe to list as a dependency in the useEffect below without
  // causing an infinite refetch loop.
  const loadData = useCallback(async () => {
    setError(null);
    try {
      // Run both requests concurrently rather than one after another -
      // they don't depend on each other's results, so there's no reason
      // to make the user wait for them sequentially.
      const [portfolioData, historyData] = await Promise.all([
        getPortfolio(PORTFOLIO_ID),
        getWithdrawalHistory(PORTFOLIO_ID),
      ]);
      setPortfolio(portfolioData);
      setHistory(historyData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  // Empty dependency array = runs once, when the component first mounts -
  // this is how the app loads its initial data on page load.
  useEffect(() => {
    loadData();
  }, [loadData]);

  async function handleWithdrawalSubmit({ amount, type }, onSuccess) {
    setSubmitting(true);
    setError(null);
    setSuccessMessage(null);
    try {
      await createWithdrawal({ portfolioId: PORTFOLIO_ID, amount, type });
      setSuccessMessage(`Withdrawal of ${formatCurrency(amount)} was processed successfully.`);
      onSuccess();
      // Re-fetch rather than manually patching local state (e.g.
      // subtracting `amount` from portfolio.balance in place) - the
      // backend is the single source of truth for the balance, and this
      // guarantees the UI always reflects exactly what was persisted.
      await loadData();
    } catch (err) {
      // err.message here is the exact string the backend's
      // GlobalExceptionHandler sent back - e.g. "Withdrawal amount
      // (91000.00) exceeds 90% of balance. Maximum allowed: 90000.00" -
      // shown to the user verbatim, since it's already specific and clear.
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="app-header__inner">
          <span className="app-header__brand">Enviro365 Investments</span>
          <h1>Withdrawal notices</h1>
        </div>
      </header>

      <main className="app-main">
        <StatusBanner type="error" message={error} onDismiss={() => setError(null)} />
        <StatusBanner
          type="success"
          message={successMessage}
          onDismiss={() => setSuccessMessage(null)}
        />

        {loading ? (
          <p className="loading-note">Loading portfolio…</p>
        ) : (
          <div className="app-grid">
            <div className="app-grid__side">
              <PortfolioSummary portfolio={portfolio} />
            </div>
            <div className="app-grid__main">
              <WithdrawalForm
                portfolio={portfolio}
                onSubmit={handleWithdrawalSubmit}
                submitting={submitting}
              />
              <WithdrawalHistoryTable history={history} portfolioId={PORTFOLIO_ID} />
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
}
