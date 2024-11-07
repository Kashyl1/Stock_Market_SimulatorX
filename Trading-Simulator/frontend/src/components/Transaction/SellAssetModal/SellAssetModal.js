import React, { useState } from 'react';
import { sellAsset } from '../../../services/TransactionService';
import './SellAssetModal.css';

const SellAssetModal = ({ currency, portfolioid, currentAmount, onClose, onSellSuccess }) => {
  const [sellType, setSellType] = useState('Crypto');
  const [amount, setAmount] = useState('');
  const [priceInUSD, setPriceInUSD] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSell = async () => {
    setError('');

    if (sellType === 'Crypto') {
      if (!amount || parseFloat(amount) <= 0) {
        setError('Please enter a valid amount of cryptocurrency.');
        return;
      }
      if (parseFloat(amount) > currentAmount) {
        setError('You cannot sell more than you own.');
        return;
      }
    } else if (sellType === 'USD') {
      if (!priceInUSD || parseFloat(priceInUSD) <= 0) {
        setError('Please enter a valid price in USD.');
        return;
      }
    }

    const sellData = {
      portfolioid,
      currencyid: currency.currencyid,
      amount: sellType === 'Crypto' ? parseFloat(amount) : null,
      priceInUSD: sellType === 'USD' ? parseFloat(priceInUSD) : null,
    };

    setLoading(true);
    try {
      await sellAsset(
        sellData.portfolioid,
        sellData.currencyid,
        sellData.amount,
        sellData.priceInUSD
      );
      alert('Asset sold successfully');
      onSellSuccess();
      onClose();
    } catch (err) {
      let errorMessage = 'An unexpected error occurred.';
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      } else if (err.response?.data) {
        const errors = Object.values(err.response.data).join(' ');
        if (errors) {
          errorMessage = errors;
        }
      }
      setError('Failed to sell asset. ' + errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Sell {currency.name}</h2>

        <div className="sell-type-selector">
          <label>
            <input
              type="radio"
              value="Crypto"
              checked={sellType === 'Crypto'}
              onChange={() => setSellType('Crypto')}
            />
            Sell {currency.symbol}
          </label>
          <label>
            <input
              type="radio"
              value="USD"
              checked={sellType === 'USD'}
              onChange={() => setSellType('USD')}
            />
            Sell for USD
          </label>
        </div>

        {sellType === 'Crypto' && (
          <label>
            Amount to Sell ({currency.symbol}):
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              min="0"
              step="0.0001"
            />
          </label>
        )}
        {sellType === 'USD' && (
          <label>
            Price in USD:
            <input
              type="number"
              value={priceInUSD}
              onChange={(e) => setPriceInUSD(e.target.value)}
              min="0"
              step="0.01"
            />
          </label>
        )}

        <p>
          <strong>Current Amount:</strong> {currentAmount} {currency.symbol}
        </p>

        {error && <p className="error-message">{error}</p>}

        <div className="modal-buttons">
          <button onClick={handleSell} disabled={loading}>
            {loading ? 'Processing...' : 'Confirm Sell'}
          </button>
          <button onClick={onClose} disabled={loading}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default SellAssetModal;
