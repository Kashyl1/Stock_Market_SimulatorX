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

  return (
    <div className="portfolio-list">
      {portfolios.map((portfolio) => (
        <div key={portfolio.portfolioid} className="portfolio-card">
          <h3>{portfolio.name}</h3>
          <p>Created At: {new Date(portfolio.createdAt).toLocaleDateString()}</p>
          <Link to={`/portfolios/${portfolio.portfolioid}`}>
            <button>View Details</button>
          </Link>
        </div>
      ))}
    </div>
  );
};

export default PortfolioList;
