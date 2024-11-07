import React, { useState, useEffect } from 'react';
import { buyAsset } from '../../../services/TransactionService';
import './BuyAssetModal.css';

const BuyAssetModal = ({ currency, portfolios, onClose }) => {
  const [buyType, setBuyType] = useState('USD');
  const [amountInUSD, setAmountInUSD] = useState('');
  const [amountOfCrypto, setAmountOfCrypto] = useState('');
  const [selectedPortfolioId, setSelectedPortfolioId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (portfolios.length > 0) {
      setSelectedPortfolioId(portfolios[0].portfolioid);
    }
  }, [portfolios]);

  const handleBuy = async () => {
    setError('');

    if (!selectedPortfolioId) {
      setError('Please select a portfolio.');
      return;
    }

    if (buyType === 'USD') {
      if (!amountInUSD || parseFloat(amountInUSD) <= 0) {
        setError('Please enter a valid amount in USD.');
        return;
      }
    } else if (buyType === 'Crypto') {
      if (!amountOfCrypto || parseFloat(amountOfCrypto) <= 0) {
        setError('Please enter a valid amount of cryptocurrency.');
        return;
      }
    }

    const purchaseData = {
      portfolioid: parseInt(selectedPortfolioId, 10),
      currencyid: currency.id,
      amountInUSD: buyType === 'USD' ? parseFloat(amountInUSD) : null,
      amountOfCrypto: buyType === 'Crypto' ? parseFloat(amountOfCrypto) : null,
    };

    setLoading(true);
    try {
      await buyAsset(
        purchaseData.portfolioid,
        purchaseData.currencyid,
        purchaseData.amountInUSD,
        purchaseData.amountOfCrypto
      );
      alert('Asset purchased successfully');
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
      setError('Failed to purchase asset. ' + errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Buy {currency.name}</h2>

        <div className="buy-type-selector">
          <label>
            <input
              type="radio"
              value="USD"
              checked={buyType === 'USD'}
              onChange={() => setBuyType('USD')}
            />
            Buy with USD
          </label>
          <label>
            <input
              type="radio"
              value="Crypto"
              checked={buyType === 'Crypto'}
              onChange={() => setBuyType('Crypto')}
            />
            Buy with {currency.symbol}
          </label>
        </div>

        {buyType === 'USD' && (
          <label>
            Amount in USD:
            <input
              type="number"
              value={amountInUSD}
              onChange={(e) => setAmountInUSD(e.target.value)}
              min="0"
              step="0.01"
            />
          </label>
        )}
        {buyType === 'Crypto' && (
          <label>
            Amount of {currency.symbol}:
            <input
              type="number"
              value={amountOfCrypto}
              onChange={(e) => setAmountOfCrypto(e.target.value)}
              min="0"
              step="0.0001"
            />
          </label>
        )}

        <label>
          Select Portfolio:
          <select
            value={selectedPortfolioId}
            onChange={(e) => setSelectedPortfolioId(e.target.value)}
          >
            <option value="">Select a portfolio</option>
            {portfolios.map((portfolio) => (
              <option key={portfolio.portfolioid} value={portfolio.portfolioid}>
                {portfolio.name}
              </option>
            ))}
          </select>
        </label>

        {error && <p className="error-message">{error}</p>}

        <div className="modal-buttons">
          <button onClick={handleBuy} disabled={loading}>
            {loading ? 'Processing...' : 'Confirm Purchase'}
          </button>
          <button onClick={onClose} disabled={loading}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default BuyAssetModal;
