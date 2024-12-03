import React from 'react';
import { deactivateTradeAlert, deleteTradeAlert } from '../../../services/TradeAlertService';
import './TradeAlertItem.css';

const TradeAlertItem = ({ tradeAlertData, onTradeAlertDeactivated, onTradeAlertDeleted }) => {
  console.log('Trade Alert Data:', tradeAlertData);

  const handleDeactivate = async () => {
    if (window.confirm('Are you sure you want to deactivate this trade alert?')) {
      try {
        await deactivateTradeAlert(tradeAlertData.tradeAlertId);
        window.alert('Trade alert has been deactivated.');
        onTradeAlertDeactivated(tradeAlertData.tradeAlertId);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
        window.alert(`Failed to deactivate trade alert. ${errorMessage}`);
      }
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this trade alert?')) {
      try {
        await deleteTradeAlert(tradeAlertData.tradeAlertId);
        window.alert('Trade alert has been deleted.');
        onTradeAlertDeleted(tradeAlertData.tradeAlertId);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
        window.alert(`Failed to delete trade alert. ${errorMessage}`);
      }
    }
  };

  return (
    <div className="table-row">
      <div className="cell">{tradeAlertData.currencyName}</div>
      <div className="cell">
        {tradeAlertData.initialPrice ? `$${tradeAlertData.initialPrice.toFixed(2)}` : 'N/A'}
      </div>
      <div className="cell">{tradeAlertData.tradeAlertType}</div>
      <div className="cell">
        {tradeAlertData.conditionPrice ? `$${tradeAlertData.conditionPrice.toFixed(2)}` : 'N/A'}
      </div>
      <div className="cell">
        {tradeAlertData.tradeAlertType === 'BUY'
          ? `$${tradeAlertData.tradeAmount.toFixed(2)}`
          : `${tradeAlertData.tradeAmount.toFixed(8)} units`}
      </div>
      <div className="cell">
        {tradeAlertData.active ? (
          <button onClick={handleDeactivate} className="deactivate-button">
            Deactivate
          </button>
        ) : (
          <button onClick={handleDelete} className="delete-button">
            Delete
          </button>
        )}
      </div>
      <div className="cell">{tradeAlertData.active ? 'Active' : 'Triggered'}</div>
    </div>
  );
};

export default TradeAlertItem;
