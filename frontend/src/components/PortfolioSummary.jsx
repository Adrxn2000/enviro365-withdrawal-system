/**
 * Pure presentational component: it receives a `portfolio` object as a
 * prop and renders it. It doesn't fetch data itself - that responsibility
 * stays in App.jsx (the "container"), which keeps this component simple
 * to read, reuse, and test: give it a portfolio, it renders a portfolio.
 */
export default function PortfolioSummary({ portfolio }) {
  if (!portfolio) return null;

  const formattedBalance = formatCurrency(portfolio.balance);

  return (
    <section className="panel" aria-labelledby="portfolio-heading">
      <div className="panel-accent" />
      <div className="panel-body">
        <h2 id="portfolio-heading">Portfolio</h2>

        <dl className="investor-details">
          <div>
            <dt>Investor</dt>
            <dd>{portfolio.investorName}</dd>
          </div>
          <div>
            <dt>Age</dt>
            <dd>{portfolio.investorAge}</dd>
          </div>
        </dl>

        <div className="balance-block">
          <span className="balance-label">Available balance</span>
          <span className="balance-figure figure">{formattedBalance}</span>
        </div>

        <h3 className="products-heading">Products</h3>
        {portfolio.products.length === 0 ? (
          <p className="empty-note">No products on this portfolio.</p>
        ) : (
          <ul className="product-list">
            {portfolio.products.map((product) => (
              <li key={product.id}>
                <span>{product.name}</span>
                <span className="product-type">{formatType(product.type)}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  );
}

// Formats a number as South African Rand, e.g. "R 250 000,00" -
// Intl.NumberFormat handles locale-correct grouping/decimal separators
// rather than us hand-rolling string manipulation on the number.
function formatCurrency(value) {
  return new Intl.NumberFormat('en-ZA', {
    style: 'currency',
    currency: 'ZAR',
  }).format(value);
}

// "MONEY_MARKET" -> "Money market" - the backend's enum-style strings are
// fine for the API, but a user shouldn't see raw enum casing in the UI.
function formatType(type) {
  return type
    .toLowerCase()
    .split('_')
    .join(' ')
    .replace(/^./, (c) => c.toUpperCase());
}
