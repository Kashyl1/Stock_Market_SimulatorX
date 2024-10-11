import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getPortfolioAssetsWithGains } from '../../../services/PortfolioService';

const PortfolioGains = () => {
  const { id } = useParams();
  const [assets, setAssets] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchGains = async () => {
      try {
        const data = await getPortfolioAssetsWithGains(id);
        setAssets(data);
      } catch (err) {
        setError('Failed to fetch portfolio gains.');
      }
    };

    fetchGains();
  }, [id]);

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div>
      <h2>Portfolio Gains/Losses</h2>
      {assets.length > 0 ? (
        <ul>
          {assets.map((asset) => (
            <li key={asset.currencyName}>
              <p>{asset.currencyName}</p>
              <p>Amount: {asset.amount}</p>
              <p>Average Purchase Price: {asset.averagePurchasePrice}</p>
              <p>Current Price: {asset.currentPrice}</p>
              <p>Gain/Loss: {asset.gainOrLoss}</p>
            </li>
          ))}
        </ul>
      ) : (
        <p>No assets found.</p>
      )}
    </div>
  );
};

export default PortfolioGains;
