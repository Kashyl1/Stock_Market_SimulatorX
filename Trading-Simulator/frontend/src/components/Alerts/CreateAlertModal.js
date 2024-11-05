import React, { useState } from 'react';
import { createAlert } from '../../services/AlertService';
import './CreateAlertModal.css';

const CreateAlertModal = ({ currency, onClose, onAlertCreated }) => {
  const [alertType, setAlertType] = useState('PERCENTAGE');
  const [percentageChange, setPercentageChange] = useState('');
  const [targetPrice, setTargetPrice] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleCreateAlert = async () => {
    setError('');

    if (
      alertType === 'PERCENTAGE' &&
      (!percentageChange || isNaN(percentageChange) || parseFloat(percentageChange) === 0)
    ) {
      setError('Percentage change must be a non-zero number.');
      return;
    }

    if (
      alertType === 'PRICE' &&
      (!targetPrice || isNaN(targetPrice) || parseFloat(targetPrice) <= 0)
    ) {
      setError('Target price must be a number greater than zero.');
      return;
    }

    const alertData = {
      currencyid: currency.currencyid,
      alertType,
      percentageChange: alertType === 'PERCENTAGE' ? parseFloat(percentageChange) : null,
      targetPrice: alertType === 'PRICE' ? parseFloat(targetPrice) : null,
    };

    setLoading(true);
    try {
      const newAlert = await createAlert(alertData);
      window.alert('Alert has been successfully created.');
      onAlertCreated(newAlert);
      onClose();
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
      setError(`Failed to create alert. ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Create Alert for {currency.name}</h2>
        <p>
          <strong>Current Price:</strong> $
          {currency.price_in_usd ? currency.price_in_usd.toFixed(2) : 'N/A'}
        </p>
        <label>
          Alert Type:
          <select
            value={alertType}
            onChange={(e) => setAlertType(e.target.value)}
          >
            <option value="PERCENTAGE">Percentage</option>
            <option value="PRICE">Price</option>
          </select>
        </label>
        {alertType === 'PERCENTAGE' && (
          <label>
            Percentage Change (%):
            <input
              type="number"
              value={percentageChange}
              onChange={(e) => setPercentageChange(e.target.value)}
              step="0.01"
            />
          </label>
        )}
        {alertType === 'PRICE' && (
          <label>
            Target Price ($):
            <input
              type="number"
              value={targetPrice}
              onChange={(e) => setTargetPrice(e.target.value)}
              step="0.01"
            />
          </label>
        )}
        {error && <p className="error-message">{error}</p>}
        <div className="modal-buttons">
          <button onClick={handleCreateAlert} disabled={loading}>
            {loading ? 'Creating...' : 'Create Alert'}
          </button>
          <button onClick={onClose} disabled={loading}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateAlertModal;
