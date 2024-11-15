import React from 'react';
import { deactivateAlert, deleteAlert } from '../../../services/MailAlertService';
import './EmailAlertItem.css';

const EmailAlertItem = ({ alertData, onAlertDeactivated, onAlertDeleted }) => {
  const handleDeactivate = async () => {
    if (window.confirm('Are you sure you want to deactivate this alert?')) {
      try {
        await deactivateAlert(alertData.alertId);
        window.alert('Alert has been deactivated.');
        onAlertDeactivated(alertData.alertId);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
        window.alert(`Failed to deactivate alert. ${errorMessage}`);
      }
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this alert?')) {
      try {
        await deleteAlert(alertData.alertId);
        window.alert('Alert has been deleted.');
        onAlertDeleted(alertData.alertId);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
        window.alert(`Failed to delete alert. ${errorMessage}`);
      }
    }
  };

  return (
    <div className="table-row">
      <div className="cell">{alertData.currencyName}</div>
      <div className="cell">${alertData.initialPrice.toFixed(2)}</div>
      <div className="cell">
        {alertData.emailAlertType === 'PERCENTAGE' ? 'Percentage' : 'Price'}
      </div>
      <div className="cell">
        {alertData.emailAlertType === 'PERCENTAGE' ? `${alertData.percentageChange}%` : `$${alertData.targetPrice.toFixed(2)}`}
      </div>
      <div className="cell"></div>
      <div className="cell">
        {alertData.active ? (
          <button onClick={handleDeactivate} className="deactivate-button">Deactivate</button>
        ) : (
          <button onClick={handleDelete} className="delete-button">Delete</button>
        )}
        <div className="cell">{alertData.active ? 'Active' : 'Triggered'}</div>
      </div>

    </div>
  );
};

export default EmailAlertItem;
