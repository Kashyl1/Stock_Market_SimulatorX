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

    const sellAmount = sellType === 'All' ? currentAmount : parseFloat(amount);

    if (sellType === 'Crypto' || sellType === 'All') {
      if (!sellAmount || sellAmount <= 0) {
        setError('Please enter a valid amount of cryptocurrency.');
        return;
      }
      if (sellAmount > currentAmount) {
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
      amount: sellType === 'Crypto' || sellType === 'All' ? sellAmount : null,
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

  const handleSellAll = () => {
    setAmount(currentAmount.toString());
  };

  const usdValue = currentAmount * currency.currentPrice;
  const estimatedUsdValue = (sellType === 'All' ? currentAmount : parseFloat(amount || 0)) * currency.currentPrice;

  return (
    <div className="sam-overlay">
      <div className="sam-modal">
        <h2>Sell</h2>

        <div className="sam-sell-type-selector">
          <label>
            <input
              type="radio"
              value="Crypto"
              checked={sellType === 'Crypto'}
              onChange={() => {
                setSellType('Crypto');
                setAmount('');
              }}
            />
            Sello {currency.currencyName}
          </label>
          <label>
            <input
              type="radio"
              value="USD"
              checked={sellType === 'USD'}
              onChange={() => {
                setSellType('USD');
                setAmount('');
              }}
            />
            Sell for USD
          </label>
          <label>
            <input
              type="radio"
              value="All"
              checked={sellType === 'All'}
              onChange={() => {
                setSellType('All');
                handleSellAll();
              }}
            />
            Sell All
          </label>
        </div>

        {sellType === 'Crypto' && (
          <label className="sam-input-label">
            Amount to Sell ({currency.symbol}):
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              min="0"
              step="0.0001"
              className="sam-input"
            />
          </label>
        )}
        {sellType === 'USD' && (
          <label className="sam-input-label">
            Price in USD:
            <input
              type="number"
              value={priceInUSD}
              onChange={(e) => setPriceInUSD(e.target.value)}
              min="0"
              step="0.01"
              className="sam-input"
            />
          </label>
        )}
        {sellType === 'All' && (
          <p className="sam-info">
            You are selling all your holdings of {currency.currencyName}
          </p>
        )}

        <p>
          <strong>Current Amount:</strong> {currentAmount}
          <br /> <br />
          <strong>Current Value:</strong>~${usdValue.toFixed(2)} USD
        </p>
        {sellType === 'Crypto' && amount && (
          <p>Estimated Value: ${estimatedUsdValue.toFixed(2)} USD</p>
        )}

        {error && <p className="sam-error-message">{error}</p>}

        <div className="sam-modal-buttons">
          <button onClick={handleSell} disabled={loading} className="sam-confirm-button">
            {loading ? 'Processing...' : 'Confirm Sell'}
          </button>
          <button onClick={onClose} disabled={loading} className="sam-cancel-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default SellAssetModal;
