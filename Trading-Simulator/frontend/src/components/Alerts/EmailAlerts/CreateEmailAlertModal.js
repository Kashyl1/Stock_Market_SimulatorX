import React, { useState, useEffect } from 'react';
import { createAlert } from '../../../services/MailAlertService';
import { getAvailableAssets } from '../../../services/CurrenciesService';
import './CreateEmailAlertModal.css';

const CreateEmailAlertModal = ({ onClose, onAlertCreated }) => {
  const [mailAlertType, setMailAlertType] = useState('PERCENTAGE');
  const [percentageChange, setPercentageChange] = useState('');
  const [targetPrice, setTargetPrice] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [currencies, setCurrencies] = useState([]);
  const [selectedCurrencyId, setSelectedCurrencyId] = useState('');
  const [selectedCurrency, setSelectedCurrency] = useState(null);

  useEffect(() => {
    const fetchCurrencies = async () => {
      try {
        const availableCurrencies = await getAvailableAssets(0, 100);
        setCurrencies(availableCurrencies.content);
        if (availableCurrencies.content.length > 0) {
          const defaultCurrency = availableCurrencies.content[0];
          setSelectedCurrencyId(defaultCurrency.currencyid);
          setSelectedCurrency(defaultCurrency);
        }
      } catch {
        setError('Failed to fetch currencies.');
      }
    };
    fetchCurrencies();
  }, []);

  useEffect(() => {
    if (selectedCurrencyId && currencies.length > 0) {
      const selected = currencies.find((c) => c.currencyid === parseInt(selectedCurrencyId));
      setSelectedCurrency(selected || null);
    }
  }, [selectedCurrencyId, currencies]);

  const handleCreateAlert = async () => {
    setError('');

    if (!selectedCurrencyId) {
      setError('Please select a currency.');
      return;
    }

    if (
      mailAlertType === 'PERCENTAGE' &&
      (!percentageChange || isNaN(percentageChange) || parseFloat(percentageChange) === 0)
    ) {
      setError('Percentage change must be a non-zero number.');
      return;
    }

    if (
      mailAlertType === 'PRICE' &&
      (!targetPrice || isNaN(targetPrice) || parseFloat(targetPrice) <= 0)
    ) {
      setError('Target price must be a number greater than zero.');
      return;
    }

    const alertData = {
      currencyid: selectedCurrencyId,
      emailAlertType: mailAlertType,
      percentageChange: mailAlertType === 'PERCENTAGE' ? parseFloat(percentageChange) : null,
      targetPrice: mailAlertType === 'PRICE' ? parseFloat(targetPrice) : null,
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

  const handleCurrencyChange = (e) => {
    const newCurrencyId = e.target.value;
    setSelectedCurrencyId(newCurrencyId);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Create Email Alert</h2>

        <label>
          Select Currency:
          <select value={selectedCurrencyId} onChange={handleCurrencyChange}>
            {currencies.map((currency) => (
              <option key={currency.currencyid} value={currency.currencyid}>
                {currency.name} ({currency.symbol})
              </option>
            ))}
          </select>
        </label>

        <p>
          <strong>Current Price:</strong> $
          {selectedCurrency && selectedCurrency.price_in_usd
            ? selectedCurrency.price_in_usd.toFixed(2)
            : 'N/A'}
        </p>

        <label>
          Alert Type:
          <select
            value={mailAlertType}
            onChange={(e) => setMailAlertType(e.target.value)}
          >
            <option value="PERCENTAGE">Percentage</option>
            <option value="PRICE">Price</option>
          </select>
        </label>
        {mailAlertType === 'PERCENTAGE' && (
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
        {mailAlertType === 'PRICE' && (
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

export default CreateEmailAlertModal;
