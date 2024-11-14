import React, { useState, useEffect } from 'react';
import { createTradeAlert } from '../../../services/TradeAlertService';
import { getAvailableAssets } from '../../../services/CurrenciesService';
import { getUserPortfolios } from '../../../services/PortfolioService';
import './CreateTradeAlertModal.css';

const CreateTradeAlertModal = ({ onClose, onTradeAlertCreated }) => {
  const [tradeAlertType, setTradeAlertType] = useState('BUY');
  const [conditionType, setConditionType] = useState('PERCENTAGE');
  const [conditionValue, setConditionValue] = useState('');
  const [tradeAmount, setTradeAmount] = useState('');
  const [portfolioId, setPortfolioId] = useState('');
  const [currencyId, setCurrencyId] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [currencies, setCurrencies] = useState([]);
  const [portfolios, setPortfolios] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const availableCurrencies = await getAvailableAssets(0, 100);
        console.log('Available Currencies:', availableCurrencies);
        setCurrencies(availableCurrencies.content || availableCurrencies);

        if (availableCurrencies.content && availableCurrencies.content.length > 0) {
          setCurrencyId(availableCurrencies.content[0].currencyid);
        }

        const userPortfoliosResponse = await getUserPortfolios();
        console.log('User Portfolios Response:', userPortfoliosResponse);
        const userPortfolios = Array.isArray(userPortfoliosResponse) ? userPortfoliosResponse : userPortfoliosResponse.content;
        setPortfolios(userPortfolios);
        if (userPortfolios.length > 0) {
          setPortfolioId(userPortfolios[0].portfolioid);
        }
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('Failed to fetch necessary data.');
      }
    };
    fetchData();
  }, []);

  const handleCreateAlert = async () => {
    setError('');

    if (!portfolioId) {
      setError('Please select a portfolio.');
      return;
    }

    if (!currencyId) {
      setError('Please select a currency.');
      return;
    }

    if (!conditionValue || isNaN(conditionValue) || parseFloat(conditionValue) <= 0) {
      setError('Condition value must be a positive number.');
      return;
    }

    if (!tradeAmount || isNaN(tradeAmount) || parseFloat(tradeAmount) <= 0) {
      setError('Trade amount must be a positive number.');
      return;
    }

    const alertData = {
      portfolioId,
      currencyId,
      tradeAlertType,
      conditionType,
      conditionValue: parseFloat(conditionValue),
      tradeAmount: parseFloat(tradeAmount),
    };

    setLoading(true);
    try {
      const newAlert = await createTradeAlert(alertData);
      console.log('New Trade Alert:', newAlert);
      window.alert('Trade Alert has been successfully created.');
      onTradeAlertCreated(newAlert);
      onClose();
    } catch (err) {
      console.error('Error creating Trade Alert:', err);
      setError(err.message || 'Failed to create trade alert.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Create Trade Alert</h2>

        <label>
          Select Portfolio:
          <select
            value={portfolioId}
            onChange={(e) => setPortfolioId(e.target.value)}
          >
            {portfolios.map((portfolio) => (
              <option key={portfolio.portfolioid} value={portfolio.portfolioid}>
                {portfolio.name}
              </option>
            ))}
          </select>
        </label>

        <label>
          Select Currency:
          <select
            value={currencyId}
            onChange={(e) => setCurrencyId(e.target.value)}
          >
            {currencies.map((currency) => (
              <option key={currency.currencyid} value={currency.currencyid}>
                {currency.name} ({currency.symbol})
              </option>
            ))}
          </select>
        </label>

        <label>
          Trade Alert Type:
          <select
            value={tradeAlertType}
            onChange={(e) => setTradeAlertType(e.target.value)}
          >
            <option value="BUY">Buy</option>
            <option value="SELL">Sell</option>
          </select>
        </label>

        <label>
          Condition Type:
          <select
            value={conditionType}
            onChange={(e) => setConditionType(e.target.value)}
          >
            <option value="PERCENTAGE">Percentage</option>
            <option value="PRICE">Price</option>
          </select>
        </label>

        <label>
          Condition Value:
          <input
            type="number"
            value={conditionValue}
            onChange={(e) => setConditionValue(e.target.value)}
            step="0.01"
          />
          {conditionType === 'PERCENTAGE' ? '%' : '$'}
        </label>

        <label>
          Trade Amount (USD):
          <input
            type="number"
            value={tradeAmount}
            onChange={(e) => setTradeAmount(e.target.value)}
            min="0"
            step="0.01"
          />
        </label>

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

export default CreateTradeAlertModal;