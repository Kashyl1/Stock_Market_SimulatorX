import React, { useState } from 'react';
import { sellAsset } from '../../../services/TransactionService';
import './SellAssetModal.css';

const SellAssetModal = ({ currency, portfolioid, currentAmount, onClose, onSellSuccess }) => {
  const [amount, setAmount] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSell = async () => {
    if (!amount || parseFloat(amount) <= 0) {
      setError('Please enter a valid amount.');
      return;
    }
    if (parseFloat(amount) > currentAmount) {
      setError('You cannot sell more than you own.');
      return;
    }

    setLoading(true);
    try {
      const sellAmount = parseFloat(amount);
      await sellAsset(portfolioid, currency.currencyid, sellAmount);
      alert('Asset sold successfully');
      onSellSuccess();
      onClose();
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
      setError('Failed to sell asset. ' + errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Sell {currency.name}</h2>
        <label>
          Amount to Sell:
          <input
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            min="0"
            step="0.0001"
          />
        </label>
        <p>Current Amount: {currentAmount}</p>
        {error && <p className="error-message">{error}</p>}
        <div className="modal-buttons">
          <button onClick={handleSell} disabled={loading}>
            {loading ? 'Processing...' : 'Confirm Sell'}
          </button>
          <button onClick={onClose} disabled={loading}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default SellAssetModal;
