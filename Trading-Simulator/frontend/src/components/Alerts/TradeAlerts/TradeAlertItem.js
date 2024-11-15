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
    <div className="table-row">
      <div className="cell">{tradeAlertData.currencyName}</div>
      <div className="cell">${tradeAlertData.initialPrice.toFixed(2)}</div>
      <div className="cell">{tradeAlertData.tradeAlertType}</div>
      <div className="cell">
        {tradeAlertData.conditionType === 'PERCENTAGE'
          ? `${tradeAlertData.conditionValue}%`
          : `$${tradeAlertData.conditionValue.toFixed(2)}`}
      </div>
      <div className="cell">
       ${tradeAlertData.tradeAmount.toFixed(2)}
      </div>
      <div className="cell">
        {tradeAlertData.active ? (
          <button onClick={handleDeactivate} className="deactivate-button">Deactivate</button>
        ) : (
          <button onClick={handleDelete} className="delete-button">Delete</button>
        )}
        <div className="cell">{tradeAlertData.active ? 'Active' : 'Triggered'}</div>
      </div>
    </div>
  );
};

export default TradeAlertItem;
