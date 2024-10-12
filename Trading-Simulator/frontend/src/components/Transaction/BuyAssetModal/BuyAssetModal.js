import React, { useState } from 'react';
import { buyAsset } from '../../../services/TransactionService';
import './BuyAssetModal.css';

const BuyAssetModal = ({ currency, portfolios, onClose }) => {
  const [amountInUSD, setAmountInUSD] = useState('');
  const [selectedPortfolioid, setSelectedPortfolioid] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleBuy = async () => {
    if (!selectedPortfolioid) {
      setError('Please select a portfolio.');
      return;
    }
    if (!amountInUSD || parseFloat(amountInUSD) <= 0) {
      setError('Please enter a valid amount.');
      return;
    }

    setLoading(true);
    try {
      const purchaseData = {
        portfolioid: parseInt(selectedPortfolioid, 10),
        currencyid: currency.id,
        amountInUSD: parseFloat(amountInUSD),
      };
      console.log('Sending purchase data:', purchaseData);

      await buyAsset(purchaseData.portfolioid, purchaseData.currencyid, purchaseData.amountInUSD);
      alert('Asset purchased successfully');
      onClose();
    } catch (err) {
      console.error('Buy asset error response data:', err.response?.data);
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
      console.error('Buy asset error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Buy {currency.name}</h2>
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
        <label>
          Select Portfolio:
          <select
            value={selectedPortfolioid}
            onChange={(e) => setSelectedPortfolioid(e.target.value)}
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
          <button onClick={onClose} disabled={loading}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default BuyAssetModal;
