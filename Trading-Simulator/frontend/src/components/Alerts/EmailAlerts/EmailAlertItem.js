import React from 'react';
import { deactivateAlert, deleteAlert } from '../../../services/MailAlertService';
import './EmailAlertItem.css';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const EmailAlertItem = ({ alertData, onAlertDeactivated, onAlertDeleted }) => {
const handleDeactivate = async () => {
  try {
    await deactivateAlert(alertData.alertId);
    notyf.success('Alert has been deactivated.');
    onAlertDeactivated(alertData.alertId);
  } catch (err) {
    const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
    notyf.error(`Failed to deactivate alert. ${errorMessage}`);
  }
};

const handleDelete = async () => {
  try {
    await deleteAlert(alertData.alertId);
    notyf.success('Alert has been deleted.');
    onAlertDeleted(alertData.alertId);
  } catch (err) {
    const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
    notyf.error(`Failed to delete alert. ${errorMessage}`);
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
