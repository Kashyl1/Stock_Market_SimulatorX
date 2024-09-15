import React, { useState, useEffect } from 'react';
import { getAvailableAssets } from '../../services/CurrenciesService';

const AvailableCurrencies = () => {
  const [currencies, setCurrencies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

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

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  return (
    <div>
      <h2>Available Cryptocurrencies</h2>
      <ul>
        {currencies.map((currency, index) => (
          <li key={index} style={{ display: 'flex', alignItems: 'center' }}>
            <img
              src={currency.image}
              alt={`${currency.name} icon`}
              width="30"
              height="30"
              style={{ marginRight: '10px' }}
            />
            {currency.name}: ${currency.current_price}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AvailableCurrencies;
