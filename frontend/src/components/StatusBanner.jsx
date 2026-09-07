/**
 * One shared component for both error and success feedback, rather than
 * two separate components - they're the same shape (an icon-less message
 * bar), just different colors and roles. `role="alert"` makes screen
 * readers announce the message as soon as it appears, which matters here
 * since this is how the user finds out a withdrawal was rejected.
 */
export default function StatusBanner({ type, message, onDismiss }) {
  if (!message) return null;

  return (
    <div className={`status-banner status-banner--${type}`} role="alert">
      <span>{message}</span>
      <button
        type="button"
        className="status-banner__dismiss"
        onClick={onDismiss}
        aria-label="Dismiss message"
      >
        ×
      </button>
    </div>
  );
}
