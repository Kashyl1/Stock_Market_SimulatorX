import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getPortfolioById } from '../../../services/PortfolioService';
import SellAssetModal from '../../../components/Transaction/SellAssetModal/SellAssetModal';
import TransactionHistory from '../../../components/TransactionHistory/TransactionHistory';
import './PortfolioDetails.css';

const PortfolioDetails = () => {
  const { id } = useParams();
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedCurrency, setSelectedCurrency] = useState(null);
  const [showSellModal, setShowSellModal] = useState(false);
  const [showTransactionHistory, setShowTransactionHistory] = useState(false);

  const fetchPortfolio = async () => {
    try {
      const data = await getPortfolioById(id);
      setPortfolio(data);
    } catch (err) {
      setError('Failed to fetch portfolio details.');
      console.error('Error fetching portfolio:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPortfolio();
  }, [id]);

  const handleSellClick = (currency) => {
    const asset = portfolio.portfolioAssets.find(a => a.currency.coinGeckoId === currency.coinGeckoId);
    if (asset) {
      setSelectedCurrency({ ...currency, amount: asset.amount });
      setShowSellModal(true);
    }
  };

  const handleCloseModal = () => {
    setShowSellModal(false);
    setSelectedCurrency(null);
  };

  const handleSellSuccess = () => {
    fetchPortfolio();
  };

  const toggleTransactionHistory = () => {
    setShowTransactionHistory(!showTransactionHistory);
  };

  if (loading) {
    return <p>Loading portfolio details...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  if (!portfolio) {
    return <p className="error-message">Portfolio not found.</p>;
  }

  return (
    <div className="portfolio-details">
      <h2>{portfolio.name}</h2>
      <p>Created At: {new Date(portfolio.createdAt).toLocaleDateString()}</p>
      <h3>Assets:</h3>
      {portfolio.portfolioAssets && portfolio.portfolioAssets.length > 0 ? (
        <div className="assets-list">
          {portfolio.portfolioAssets.map((asset) => (
            <div key={asset.portfolioAssetID} className="asset-card">
              <img src={asset.currency.image} alt={`${asset.currency.name} icon`} />
              <h4>{asset.currency.name} ({asset.currency.symbol.toUpperCase()})</h4>
              <p>Amount: {asset.amount}</p>
              <p>Average Purchase Price: ${asset.averagePurchasePrice.toFixed(2)}</p>
              <button onClick={() => handleSellClick(asset.currency)}>Sell</button>
            </div>
          ))}
        </div>
      ) : (
        <p>No assets found in this portfolio.</p>
      )}

      <button onClick={toggleTransactionHistory}>
        {showTransactionHistory ? 'Hide' : 'View'} Transaction History
      </button>

      {showTransactionHistory && (
        <TransactionHistory portfolioId={portfolio.portfolioID} />
      )}

      {showSellModal && selectedCurrency && (
        <SellAssetModal
          currency={selectedCurrency}
          portfolioID={portfolio.portfolioID}
          currentAmount={selectedCurrency.amount}
          onClose={handleCloseModal}
          onSellSuccess={handleSellSuccess}
        />
      )}
    </div>
  );
};

export default PortfolioDetails;
