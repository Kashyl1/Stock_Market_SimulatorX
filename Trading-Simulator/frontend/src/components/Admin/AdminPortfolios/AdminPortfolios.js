import React, { useEffect, useState } from 'react';
import { getPortfolios, deletePortfolio } from '../../../services/AdminService';
import Sidebar from '../../../pages/Sidebar/Sidebar';
import UserTransactions from '../../../components/Admin/AdminTransactions/UserTransactions';

const AdminPortfolios = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 20;


  const fetchPortfolios = async (page) => {
    setIsLoading(true);
    try {
      const data = await getPortfolios(page, pageSize);
      setPortfolios(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('Error fetching portfolios:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePortfolioClick = (portfolio) => {
    setSelectedPortfolio(portfolio);
  };

  const handleDeletePortfolio = async (portfolioId) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this portfolio?');
    if (confirmDelete) {
      try {
        await deletePortfolio(portfolioId);
        fetchPortfolios(currentPage);
      } catch (error) {
        console.error('Error deleting portfolio:', error);
      }
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      setCurrentPage((prevPage) => prevPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      setCurrentPage((prevPage) => prevPage - 1);
    }
  };

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

  useEffect(() => {
    fetchPortfolios(currentPage);
  }, [currentPage]);

  if (isLoading) {
    return <div className="loader">Loading...</div>;
  }

  return (
    <div className="portfolio-container">
      {selectedPortfolio ? (
        <div className="portfolio-details">
          <button onClick={() => setSelectedPortfolio(null)} className="cell">
            Back to All Portfolios
          </button>
          <h2>{selectedPortfolio.name}</h2>
          <p>Created At: {formatDateTime(selectedPortfolio.createdAt)}</p>
          <p>Updated At: {formatDateTime(selectedPortfolio.updatedAt)}</p>
          <h3>Assets:</h3>
          {selectedPortfolio.portfolioAssets && selectedPortfolio.portfolioAssets.length > 0 ? (
            <div className="assets-table">
              <div className="table-header">
                <div className="header-cell">Name</div>
                <div className="header-cell">Amount</div>
                <div className="header-cell">Average Purchase Price</div>
                <div className="header-cell">Current Price</div>
                <div className="header-cell">Gain/Loss</div>
              </div>

              {selectedPortfolio.portfolioAssets.map((asset) => (
                <div className="table-row" key={asset.currencyid}>
                  <div className="cell currency-info">
                    <img src={asset.imageUrl} alt={asset.currencyName} className="currency-icon" />
                    <span className="currency-name">{asset.currencyName}</span>
                  </div>
                  <div className="cell">{asset.amount}</div>
                  <div className="cell">${asset.averagePurchasePrice.toFixed(2)}</div>
                  <div className="cell">${asset.currentPrice.toFixed(2)}</div>
                  <div className="cell">
                    <span className={asset.gainOrLoss >= 0 ? 'positive' : 'negative'}>
                      {asset.gainOrLoss !== null && (asset.gainOrLoss >= 0 ? '+' : '')}${asset.gainOrLoss ? asset.gainOrLoss.toFixed(2) : 'N/A'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p>No assets found in this portfolio.</p>
          )}

          <UserTransactions portfolioId={selectedPortfolio.portfolioid} />
        </div>
      ) : (
        <div>
          <div className="portfolio-list">
            {portfolios.map((portfolio) => (
              <div
                key={portfolio.portfolioid}
                className="portfolio-card"
                onClick={() => handlePortfolioClick(portfolio)}
              >
                <h2 className="portfolio-name">{portfolio.name}</h2>
                <p className="portfolio-dates">
                  Created: {new Date(portfolio.createdAt).toLocaleDateString()} | Updated: {new Date(portfolio.updatedAt).toLocaleDateString()}
                </p>
                <button onClick={() => handlePortfolioClick(portfolio)}>View Details</button>
                <button onClick={() => handleDeletePortfolio(portfolio.portfolioid)} className="delete-button">
                  Delete Portfolio
                </button>
              </div>
            ))}
          </div>
          <div className="pagination-controls">
            <button onClick={handlePrevPage} disabled={currentPage === 0}>
              Previous
            </button>
            <span>
              Page {currentPage + 1} of {totalPages}
            </span>
            <button onClick={handleNextPage} disabled={currentPage === totalPages - 1}>
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPortfolios;
