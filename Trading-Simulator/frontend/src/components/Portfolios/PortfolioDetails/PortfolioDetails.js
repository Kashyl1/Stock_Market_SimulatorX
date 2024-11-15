import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getPortfolioByid, getPortfolioAssetsWithGains, getTotalPortfolioGainOrLoss } from '../../../services/PortfolioService';
import SellAssetModal from '../../../components/Transaction/SellAssetModal/SellAssetModal';
import TransactionHistory from '../../../components/TransactionHistory/TransactionHistory';
import TransactionHistoryHeightAdjuster from './TransactionHistoryHeightAdjuster';
import './PortfolioDetails.css';
import Sidebar from '../../../pages/Sidebar/Sidebar';

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
  const [initialized, setInitialized] = useState(false);

  const fetchPortfolio = async () => {
    try {
      const portfolioData = await getPortfolioByid(id);
      setPortfolio(portfolioData);

      const gainsData = await getPortfolioAssetsWithGains(id);
      setAssetsWithGains(gainsData);

      const totalGainOrLossData = await getTotalPortfolioGainOrLoss(id);
      setTotalGainOrLoss(totalGainOrLossData);
    } catch (err) {
      let errorMessage = 'Failed to fetch portfolio details.';
      if (err.response?.data?.message) {
        errorMessage = err.response.data.message;
      }
      setError(errorMessage);
    } finally {
      setLoading(false);
      setInitialized(true);
    }
  };

  useEffect(() => {
    fetchPortfolio();
    const interval = setInterval(fetchPortfolio, 60000);

    return () => clearInterval(interval);
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
    <div className="main-page">
      <Sidebar />
      <div className="portfolio-details">
        <h2>{portfolio.name}</h2>
        <p>Created At: {formatDateTime(portfolio.createdAt)}</p>
        <p>Updated At: {formatDateTime(portfolio.updatedAt)}</p>
        <h3>Assets:</h3>
        {assetsWithGains && assetsWithGains.length > 0 ? (
          <div className="assets-table">
            <div className="table-header">
              <div className="header-cell">Name</div>
              <div className="header-cell">Amount</div>
              <div className="header-cell">Average Purchase Price</div>
              <div className="header-cell">Current Price</div>
              <div className="header-cell">Gain/Loss</div>
              <div className="header-cell">Action</div>
            </div>

            {assetsWithGains.map((asset) => (
              <div className="table-row" key={asset.currencyName}>
                <div className="cell currency-info">
                  <img src={asset.imageUrl} alt={asset.currencyName} className="currency-icon" />
                  <span className="currency-name">{asset.currencyName}</span>
                </div>
                <div className="cell">{asset.amount}</div>
                <div className="cell">${asset.averagePurchasePrice.toFixed(2)}</div>
                <div className="cell">${asset.currentPrice.toFixed(2)}</div>
                <div className="cell">
                  <span className={`currency-change ${asset.gainOrLoss >= 0 ? 'positive' : 'negative'}`}>
                    {asset.gainOrLoss >= 0 ? '+' : ''}${asset.gainOrLoss.toFixed(2)}
                  </span>
                </div>
                <div className="cell">
                  <button className="action-button" onClick={() => handleSellClick(asset)}>Sell</button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p>No assets found in this portfolio.</p>
        )}

        <button
          onClick={toggleTransactionHistory}
          className="toggle-button"
        >
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

      <TransactionHistoryHeightAdjuster
        showTransactionHistory={showTransactionHistory}
        initialized={initialized}
      />
    </div>
  );
};

export default PortfolioDetails;
