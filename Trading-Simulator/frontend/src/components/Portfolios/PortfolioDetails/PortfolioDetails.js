import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getPortfolioByid, getPortfolioAssetsWithGains, getTotalPortfolioGainOrLoss } from '../../../services/PortfolioService';
import SellAssetModal from '../../../components/Transaction/SellAssetModal/SellAssetModal';
import TransactionHistory from '../../../components/TransactionHistory/TransactionHistory';
import './PortfolioDetails.css';

const PortfolioDetails = () => {
  const { id } = useParams();
  const [portfolio, setPortfolio] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [assetsWithGains, setAssetsWithGains] = useState([]);
  const [totalGainOrLoss, setTotalGainOrLoss] = useState(0);
  const [selectedCurrency, setSelectedCurrency] = useState(null);
  const [showSellModal, setShowSellModal] = useState(false);
  const [showTransactionHistory, setShowTransactionHistory] = useState(false);

  const fetchPortfolio = async () => {
    try {
      const portfolioData = await getPortfolioByid(id);
      setPortfolio(portfolioData);

      const gainsData = await getPortfolioAssetsWithGains(id);
      setAssetsWithGains(gainsData);

      const totalGainOrLossData = await getTotalPortfolioGainOrLoss(id);
      setTotalGainOrLoss(totalGainOrLossData);
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

  const handleSellClick = (asset) => {
    setSelectedCurrency(asset);
    setShowSellModal(true);
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

  const formatDateTime = (dateTimeStr) => {
    const options = {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return new Date(dateTimeStr).toLocaleString(undefined, options);
  };

  return (
    <div className="portfolio-details">
      <h2>{portfolio.name}</h2>
      <p>Created At: {formatDateTime(portfolio.createdAt)}</p>
      <p>Updated At: {formatDateTime(portfolio.updatedAt)}</p>
      <h3>Assets:</h3>
      {assetsWithGains && assetsWithGains.length > 0 ? (
        <div className="assets-list">
          {assetsWithGains.map((asset) => (
            <div key={asset.currencyName} className="asset-card">
              <h4>{asset.currencyName}</h4>
              <p>Amount: {asset.amount}</p>
              <p>Average Purchase Price: ${asset.averagePurchasePrice.toFixed(2)}</p>
              <p>Current Price: ${asset.currentPrice.toFixed(2)}</p>
              <p>Gain/Loss: {asset.gainOrLoss >= 0 ? '+' : ''}${asset.gainOrLoss.toFixed(2)}</p>
              <button onClick={() => handleSellClick(asset)}>Sell</button>
            </div>
          ))}
        </div>
      ) : (
        <p>No assets found in this portfolio.</p>
      )}

      <h3>Total Portfolio Gain/Loss: {totalGainOrLoss >= 0 ? '+' : ''}${totalGainOrLoss.toFixed(2)}</h3>

      <button onClick={toggleTransactionHistory}>
        {showTransactionHistory ? 'Hide' : 'View'} Transaction History
      </button>

      {showTransactionHistory && (
        <TransactionHistory portfolioid={portfolio.portfolioid} />
      )}

      {showSellModal && selectedCurrency && (
        <SellAssetModal
          currency={selectedCurrency}
          portfolioid={portfolio.portfolioid}
          currentAmount={selectedCurrency.amount}
          onClose={handleCloseModal}
          onSellSuccess={handleSellSuccess}
        />
      )}
    </div>
  );
};

export default PortfolioDetails;
