import { useState } from 'react';

/**
 * Controlled form: every input's value lives in React state (useState),
 * not in the DOM itself. That's what "controlled" means - React is the
 * single source of truth for what's in the fields, which is what lets us
 * validate and reset the form programmatically after a successful submit.
 *
 * `onWithdrawn` is a callback prop - this component doesn't know what
 * happens after a successful withdrawal (refresh the balance? reload
 * history?). It just calls the function its parent (App.jsx) gave it,
 * keeping this component decoupled from that decision.
 */
export default function WithdrawalForm({ portfolio, onSubmit, submitting }) {
  const [amount, setAmount] = useState('');
  const [type, setType] = useState('GENERAL');
  const [clientError, setClientError] = useState(null);

  function handleSubmit(event) {
    // Forms reload the page by default on submit - this stops that so we
    // can handle it with fetch() instead.
    event.preventDefault();
    setClientError(null);

    // UI-side validation: catches obviously-invalid input before we even
    // make a network call. This does NOT replace the backend's checks -
    // the backend re-validates everything regardless, since a user could
    // call the API directly and skip the UI entirely. This is purely a
    // faster feedback loop for someone using the form normally.
    const numericAmount = Number(amount);
    if (!amount || Number.isNaN(numericAmount) || numericAmount <= 0) {
      setClientError('Enter an amount greater than zero.');
      return;
    }
    if (portfolio && numericAmount > Number(portfolio.balance)) {
      setClientError(
        `Amount exceeds the available balance of ${new Intl.NumberFormat('en-ZA', {
          style: 'currency',
          currency: 'ZAR',
        }).format(portfolio.balance)}.`
      );
      return;
    }

    // Delegate the actual submission (and its own error handling) up to
    // App.jsx, which owns the API call and shared error/success banner.
    onSubmit({ amount: numericAmount, type }, () => {
      // Reset the form only on success - App.jsx calls this second
      // argument after createWithdrawal() resolves without throwing.
      setAmount('');
      setType('GENERAL');
    });
  }

  return (
    <section className="panel" aria-labelledby="withdrawal-heading">
      <div className="panel-accent" />
      <div className="panel-body">
        <h2 id="withdrawal-heading">New withdrawal</h2>

        <form onSubmit={handleSubmit} noValidate>
          <label className="field">
            <span>Amount (ZAR)</span>
            <input
              type="number"
              step="0.01"
              min="0.01"
              inputMode="decimal"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="0.00"
              disabled={submitting}
              required
            />
          </label>

          <fieldset className="field">
            <legend>Withdrawal type</legend>
            <label className="radio-option">
              <input
                type="radio"
                name="type"
                value="GENERAL"
                checked={type === 'GENERAL'}
                onChange={() => setType('GENERAL')}
                disabled={submitting}
              />
              General
            </label>
            <label className="radio-option">
              <input
                type="radio"
                name="type"
                value="RETIREMENT"
                checked={type === 'RETIREMENT'}
                onChange={() => setType('RETIREMENT')}
                disabled={submitting}
              />
              Retirement
            </label>
            {type === 'RETIREMENT' && (
              <p className="field-hint">Only allowed for investors older than 65.</p>
            )}
          </fieldset>

          {clientError && (
            <p className="form-error" role="alert">
              {clientError}
            </p>
          )}

          <button type="submit" className="btn-primary" disabled={submitting}>
            {submitting ? 'Submitting…' : 'Submit withdrawal'}
          </button>
        </form>
      </div>
    </section>
  );
}
