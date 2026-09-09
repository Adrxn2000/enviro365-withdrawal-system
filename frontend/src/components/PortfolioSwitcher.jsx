export default function PortfolioSwitcher({ portfolios, selectedId, onSelect }) {
  if (portfolios.length === 0) return null;

  return (
    <div className="portfolio-switcher" role="radiogroup" aria-label="Viewing portfolio">
      <span className="portfolio-switcher__label">Viewing portfolio</span>
      {portfolios.map((portfolio) => (
        <label key={portfolio.portfolioId} className="radio-option">
          <input
            type="radio"
            name="portfolio"
            value={portfolio.portfolioId}
            checked={selectedId === portfolio.portfolioId}
            onChange={() => onSelect(portfolio.portfolioId)}
          />
          {portfolio.investorName} (age {portfolio.investorAge})
        </label>
      ))}
    </div>
  );
}