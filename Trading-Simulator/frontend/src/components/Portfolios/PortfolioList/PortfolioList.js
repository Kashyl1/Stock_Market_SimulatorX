import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../Portfolios.css';
import {deletePortfolio} from '../../../services/PortfolioService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const PortfolioList = ({ portfolios, onDeleteSuccess }) => {
  const navigate = useNavigate();

  const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return 'N/A';
    const options = {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return new Date(dateTimeStr).toLocaleString(undefined, options);
  };

  const handleDeletePortfolio = async (portfolioid) => {
      try {
        await deletePortfolio(portfolioid);
        notyf.success('Portfolio deleted successfully!');
        onDeleteSuccess(portfolioid);
      } catch (error) {
        console.error('Error deleting portfolio:', error);
        notyf.error('Failed to delete the portfolio.');
      }

  };

  return (
    <div className="portfolio-list">
      {portfolios.map((portfolio) => (
        <div key={portfolio.portfolioid} className="portfolio-card">
          <h3>{portfolio.name}</h3>
          <p>Created At: {formatDateTime(portfolio.createdAt)}</p>
          <p>Updated At: {formatDateTime(portfolio.updatedAt)}</p>
          <div className="portfolio-actions">
            <button
              onClick={() => navigate(`/portfolios/${portfolio.portfolioid}`)}
              className="view-button"
            >
              View Details
            </button>
            <button
              className="delete-button"
              onClick={() => handleDeletePortfolio(portfolio.portfolioid)}
            >
              Delete
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default PortfolioList;
