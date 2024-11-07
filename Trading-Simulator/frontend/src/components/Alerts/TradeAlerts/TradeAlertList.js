import React from 'react';
import TradeAlertItem from './TradeAlertItem';
import './TradeAlertList.css';

const TradeAlertList = ({ tradeAlerts, loading, error, onTradeAlertDeactivated, onTradeAlertDeleted }) => {
  if (loading) {
    return <p>Loading trade alerts...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  if (tradeAlerts.length === 0) {
    return <p>You have no set trade alerts.</p>;
  }

  return (
    <div className="trade-alert-list">
      {tradeAlerts.map((tradeAlert) => (
        <TradeAlertItem
          key={tradeAlert.tradeAlertId}
          tradeAlertData={tradeAlert}
          onTradeAlertDeactivated={onTradeAlertDeactivated}
          onTradeAlertDeleted={onTradeAlertDeleted}
        />
      ))}
    </div>
  );
};

export default TradeAlertList;
