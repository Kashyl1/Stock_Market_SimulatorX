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
    <div className="email-alert-item">
      <div className="email-alert-details">
        <p>
          <strong>Currency:</strong> {alertData.currencyName}
        </p>
        <p>
          <strong>Created At Price:</strong> ${alertData.initialPrice.toFixed(2)}
        </p>
        <p>
          <strong>Type:</strong> {alertData.emailAlertType === 'PERCENTAGE' ? 'Percentage' : 'Price'}
        </p>
        {alertData.emailAlertType === 'PERCENTAGE' && (
          <p>
            <strong>Change:</strong> {alertData.percentageChange}%
          </p>
        )}
        {alertData.emailAlertType === 'PRICE' && (
          <p>
            <strong>Target Price:</strong> ${alertData.targetPrice.toFixed(2)}
          </p>
        )}
        <p>
          <strong>Status:</strong> {alertData.active ? 'Active' : 'Triggered'}
        </p>
      </div>
      <div className="email-alert-actions">
        {alertData.active ? (
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

export default EmailAlertItem;
