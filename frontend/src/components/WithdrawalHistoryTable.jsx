import { useState } from 'react';
import { buildExportUrl } from '../api/client';

/**
 * Displays the withdrawal history and lets the user filter it (by type
 * and/or date range) before either viewing the filtered rows or
 * downloading them as CSV. Filtering happens entirely client-side against
 * the `history` array already loaded - the CSV export is the only request
 * that goes back to the server with the filters, matching the brief's
 * "export CSV statements with filtering" requirement specifically.
 */
export default function WithdrawalHistoryTable({ history, portfolioId }) {
  const [typeFilter, setTypeFilter] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');

  const filteredHistory = history.filter((entry) => {
    if (typeFilter && entry.type !== typeFilter) return false;
    const entryDate = entry.createdAt.slice(0, 10); // "2026-09-05T14:00:00" -> "2026-09-05"
    if (fromDate && entryDate < fromDate) return false;
    if (toDate && entryDate > toDate) return false;
    return true;
  });

  const exportUrl = buildExportUrl(portfolioId, {
    type: typeFilter || undefined,
    from: fromDate || undefined,
    to: toDate || undefined,
  });

  return (
    <section className="panel" aria-labelledby="history-heading">
      <div className="panel-accent" />
      <div className="panel-body">
        <div className="history-header">
          <h2 id="history-heading">Withdrawal history</h2>
          {/* A plain <a> download link, not a button + fetch handler -
              the browser handles the actual file save using the backend's
              Content-Disposition header (see client.js buildExportUrl). */}
          <a className="btn-secondary" href={exportUrl} download>
            Download CSV
          </a>
        </div>

        <div className="filters">
          <label className="field field-inline">
            <span>Type</span>
            <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
              <option value="">All</option>
              <option value="GENERAL">General</option>
              <option value="RETIREMENT">Retirement</option>
            </select>
          </label>
          <label className="field field-inline">
            <span>From</span>
            <input type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
          </label>
          <label className="field field-inline">
            <span>To</span>
            <input type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} />
          </label>
        </div>

        {filteredHistory.length === 0 ? (
          <p className="empty-note">
            {history.length === 0
              ? 'No withdrawals yet. Submitted withdrawals will appear here.'
              : 'No withdrawals match the selected filters.'}
          </p>
        ) : (
          <table className="history-table">
            <thead>
              <tr>
                <th scope="col">Date</th>
                <th scope="col">Type</th>
                <th scope="col">Amount</th>
                <th scope="col">Balance after</th>
              </tr>
            </thead>
            <tbody>
              {filteredHistory.map((entry) => (
                <tr key={entry.id}>
                  <td>{formatDate(entry.createdAt)}</td>
                  <td>{entry.type === 'RETIREMENT' ? 'Retirement' : 'General'}</td>
                  <td className="figure">{formatCurrency(entry.amount)}</td>
                  <td className="figure">{formatCurrency(entry.balanceAfter)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
}

function formatDate(isoString) {
  return new Intl.DateTimeFormat('en-ZA', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(isoString));
}
