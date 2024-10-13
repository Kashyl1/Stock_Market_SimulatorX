import React from 'react';
import { Link } from 'react-router-dom';
import '../Portfolios.css';

const PortfolioList = ({ portfolios }) => {

  if (!Array.isArray(portfolios)) {
    return <p className="error-message">Invalid portfolios data.</p>;
  }

  if (portfolios.length === 0) {
    return <p>No portfolios found. Create one to get started!</p>;
  }

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

  return (
    <div className="portfolio-list">
      {portfolios.map((portfolio) => (
        <div key={portfolio.portfolioid} className="portfolio-card">
          <h3>{portfolio.name}</h3>
          <p>Created At: {formatDateTime(portfolio.createdAt)}</p>
          <p>Updated At: {formatDateTime(portfolio.updatedAt)}</p>
          <Link to={`/portfolios/${portfolio.portfolioid}`}>
            <button>View Details</button>
          </Link>
        </div>
      ))}
    </div>
  );
};

export default PortfolioList;
