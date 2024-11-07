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

  const calculateTargetPrice = (initialPrice, conditionType, conditionValue, tradeAlertType) => {
    if (
      initialPrice === null ||
      initialPrice === undefined ||
      conditionValue === null ||
      conditionValue === undefined
    ) {
      return 'N/A';
    }

    if (conditionType === 'PRICE') {
      return `$${conditionValue.toFixed(2)}`;
    }

    if (conditionType === 'PERCENTAGE') {
      let targetPrice;
      if (tradeAlertType === 'BUY') {
        targetPrice = initialPrice * (1 - conditionValue / 100);
      } else if (tradeAlertType === 'SELL') {
        targetPrice = initialPrice * (1 + conditionValue / 100);
      } else {
        return 'N/A';
      }
      return `$${targetPrice.toFixed(2)}`;
    }

    return 'N/A';
  };

  const targetPrice = calculateTargetPrice(
    tradeAlertData.initialPrice,
    tradeAlertData.conditionType,
    tradeAlertData.conditionValue,
    tradeAlertData.tradeAlertType
  );

  return (
    <div className="trade-alert-item">
      <div className="trade-alert-details">
        <p>
          <strong>Currency:</strong> {tradeAlertData.currencyName}
        </p>
        <p>
          <strong>Created At Price:</strong> $
          {tradeAlertData.initialPrice !== null && tradeAlertData.initialPrice !== undefined
            ? tradeAlertData.initialPrice.toFixed(2)
            : 'N/A'}
        </p>
        <p>
          <strong>Type:</strong> {tradeAlertData.tradeAlertType}
        </p>
        <p>
          <strong>Condition:</strong>{' '}
          {tradeAlertData.conditionType === 'PERCENTAGE'
            ? `${tradeAlertData.conditionValue}%`
            : `$${tradeAlertData.conditionValue.toFixed(2)}`}
        </p>
        <p>
          <strong>Trade Amount:</strong>{' '}
          {tradeAlertData.conditionType === 'PERCENTAGE'
            ? `${tradeAlertData.tradeAmount}%`
            : `$${tradeAlertData.tradeAmount.toFixed(2)}`}
        </p>
        <p>
          <strong>Target Price:</strong> {targetPrice}
        </p>
        <p>
          <strong>Status:</strong> {tradeAlertData.active ? 'Active' : 'Triggered'}
        </p>
      </div>
      <div className="trade-alert-actions">
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
    </div>
  );
};

export default TradeAlertItem;
