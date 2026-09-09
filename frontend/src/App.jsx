import { useEffect, useState, useCallback } from 'react';
import './App.css';
import { getAllPortfolios, getPortfolio, getWithdrawalHistory, createWithdrawal } from './api/client';
import PortfolioSwitcher from './components/PortfolioSwitcher';
import PortfolioSummary from './components/PortfolioSummary';
import WithdrawalForm from './components/WithdrawalForm';
import WithdrawalHistoryTable from './components/WithdrawalHistoryTable';
import StatusBanner from './components/StatusBanner';

export default function App() {
  // The list of ALL portfolios (for the switcher) is separate from the
  // currently-selected one's full detail + history - two different
  // pieces of state because they come from two different endpoints and
  // change at different times.
  const [portfolioList, setPortfolioList] = useState([]);
  const [selectedId, setSelectedId] = useState(null);

  const [portfolio, setPortfolio] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Runs once on mount: fetch the list of investors/portfolios for the
  // switcher, and default the selection to the first one returned.
  useEffect(() => {
    async function loadPortfolioList() {
      try {
        const list = await getAllPortfolios();
        setPortfolioList(list);
        if (list.length > 0) {
          setSelectedId(list[0].portfolioId);
        }
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    }
    loadPortfolioList();
  }, []);

  // Loads the FULL detail (products, etc.) + history for whichever
  // portfolio is currently selected. useCallback + selectedId as a
  // dependency means this function is recreated only when the selected
  // portfolio actually changes, which is also what the effect below
  // watches for.
  const loadSelectedPortfolio = useCallback(async (portfolioId) => {
    setError(null);
    try {
      const [portfolioData, historyData] = await Promise.all([
        getPortfolio(portfolioId),
        getWithdrawalHistory(portfolioId),
      ]);
      setPortfolio(portfolioData);
      setHistory(historyData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  // Whenever selectedId changes (initial load, or the user clicks a
  // different radio button), reload that portfolio's detail + history.
  useEffect(() => {
    if (selectedId !== null) {
      setLoading(true);
      loadSelectedPortfolio(selectedId);
    }
  }, [selectedId, loadSelectedPortfolio]);

  async function handleWithdrawalSubmit({ amount, type }, onSuccess) {
    setSubmitting(true);
    setError(null);
    setSuccessMessage(null);
    try {
      await createWithdrawal({ portfolioId: selectedId, amount, type });
      setSuccessMessage(`Withdrawal of ${formatCurrency(amount)} was processed successfully.`);
      onSuccess();
      await loadSelectedPortfolio(selectedId);
    } catch (err) {
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

        {portfolioList.length > 1 && (
          <PortfolioSwitcher
            portfolios={portfolioList}
            selectedId={selectedId}
            onSelect={setSelectedId}
          />
        )}

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
              <WithdrawalHistoryTable history={history} portfolioId={selectedId} />
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