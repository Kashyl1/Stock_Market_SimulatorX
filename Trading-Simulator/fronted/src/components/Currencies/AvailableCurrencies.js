import React, { useState, useEffect } from 'react';
import { getAvailableAssets } from '../../services/CurrenciesService';
import './AvailableCurrencies.css';

const AvailableCurrencies = () => {
  const [currencies, setCurrencies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchAssets = async () => {
      try {
        const cachedAssets = localStorage.getItem('availableAssets');
        if (cachedAssets) {
          setCurrencies(JSON.parse(cachedAssets));
        } else {
          const assets = await getAvailableAssets();
          setCurrencies(assets);
          localStorage.setItem('availableAssets', JSON.stringify(assets));
        }
      } catch (err) {
        setError('Failed to fetch available assets.');
      } finally {
        setLoading(false);
      }
    };

    fetchAssets();
  }, []);

  const filteredCurrencies = currencies.filter((currency) =>
    currency.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  return (
    <div>
      <h2 className="available-cryptocurrencies">Explore the economics of cryptocurrencies</h2>


      <div className="search-container">
        <span className="search-icon">&#128269;</span>
        <input
          type="text"
          placeholder="Search"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>


      <div className="currency-list">
        {filteredCurrencies.map((currency, index) => (
          <div key={index} className="currency-card">
            <img
              src={currency.image}
              alt={`${currency.name} icon`}
            />
            <h2>{currency.name}</h2>
            <p className="currency-price">${currency.current_price}</p>
            <p
              className={`currency-change ${
                currency.price_change_percentage_24h > 0 ? 'positive' : 'negative'
              }`}
            >
              {currency.price_change_percentage_24h > 0 ? '+' : ''}
              {currency.price_change_percentage_24h}%
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AvailableCurrencies;
