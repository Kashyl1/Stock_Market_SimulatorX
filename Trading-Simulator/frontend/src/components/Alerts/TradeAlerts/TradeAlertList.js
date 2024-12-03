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

  if (!tradeAlerts || tradeAlerts.length === 0) {
    return <p>You have no set trade alerts.</p>;
  }

  return (
    <div className="trade-alert-list">
      <div className="assets-table">
        <div className="table-header">
          <div className="header-cell">Currency</div>
          <div className="header-cell">Initial Price</div>
          <div className="header-cell">Alert Type</div>
          <div className="header-cell">Condition Price</div>
          <div className="header-cell">Trade Amount</div>
          <div className="header-cell">Actions</div>
          <div className="header-cell">Status</div>
        </div>

        <div className="table-body">
          {tradeAlerts.map((tradeAlert) => (
            <TradeAlertItem
              key={tradeAlert.tradeAlertId}
              tradeAlertData={tradeAlert}
              onTradeAlertDeactivated={onTradeAlertDeactivated}
              onTradeAlertDeleted={onTradeAlertDeleted}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default TradeAlertList;
