import React, { useState, useEffect } from 'react';
import { createTradeAlert } from '../../../services/TradeAlertService';
import { getAvailableAssets } from '../../../services/CurrenciesService';
import { getUserPortfolios } from '../../../services/PortfolioService';
import './CreateTradeAlertModal.css';

const CreateTradeAlertModal = ({ onClose, onTradeAlertCreated }) => {
  const [tradeAlertType, setTradeAlertType] = useState('BUY');
  const [conditionPrice, setConditionPrice] = useState('');
  const [tradeAmount, setTradeAmount] = useState('');
  const [orderType, setOrderType] = useState('LIMIT');
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
        setCurrencies(availableCurrencies.content || availableCurrencies);

        if (availableCurrencies.content && availableCurrencies.content.length > 0) {
          setCurrencyId(availableCurrencies.content[0].currencyid);
        }

        const userPortfoliosResponse = await getUserPortfolios();
        const userPortfolios = Array.isArray(userPortfoliosResponse)
          ? userPortfoliosResponse
          : userPortfoliosResponse.content;
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

    if (!conditionPrice || isNaN(conditionPrice) || parseFloat(conditionPrice) <= 0) {
      setError('Condition price must be a positive number.');
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
      conditionPrice: parseFloat(conditionPrice),
      tradeAmount: parseFloat(tradeAmount),
      orderType,
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
          Order Type:
          <select
            value={orderType}
            onChange={(e) => setOrderType(e.target.value)}
          >
            <option value="LIMIT">Limit</option>
            <option value="STOP">Stop</option>
          </select>
        </label>

        <label>
          Condition Price ($):
          <input
            type="number"
            value={conditionPrice}
            onChange={(e) => setConditionPrice(e.target.value)}
            step="0.01"
          />
        </label>

        <label>
          Trade Amount ({tradeAlertType === 'BUY' ? 'USD' : 'Currency Units'}):
          <input
            type="number"
            value={tradeAmount}
            onChange={(e) => setTradeAmount(e.target.value)}
            min="0"
            step="0.00000001"
          />
        </label>

        {error && <p className="error-message">{error}</p>}

        <div className="modal-buttons">
          <button onClick={handleCreateAlert} disabled={loading}>
            {loading ? 'Creating...' : 'Create Alert'}
          </button>
          <button onClick={onClose} disabled={loading} className="cancel-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateTradeAlertModal;
