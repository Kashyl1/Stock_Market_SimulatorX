import React, { useState, useEffect } from 'react';
import { getUserPortfolios } from '../../services/PortfolioService';
import CreatePortfolio from '../../components/Portfolios/CreatePortfolio/CreatePortfolio';
import PortfolioList from '../../components/Portfolios/PortfolioList/PortfolioList';
import './Portfolios.css';
import Sidebar from '../../pages/Sidebar/Sidebar';

const PortfoliosPage = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [error, setError] = useState('');

  const fetchPortfolios = async () => {
    try {
      const data = await getUserPortfolios();
      setPortfolios(data);
    } catch (err) {
      setError('Failed to fetch portfolios. Please try again.');
    }
  };

  useEffect(() => {
    fetchPortfolios();
  }, []);

  const handlePortfolioCreated = (newPortfolio) => {
    setPortfolios((prevPortfolios) => [...prevPortfolios, newPortfolio]);
  };

  const handleDeleteSuccess = (deletedPortfolioId) => {
      setPortfolios((prevPortfolios) =>
        prevPortfolios.filter((portfolio) => portfolio.portfolioid !== deletedPortfolioId)
      );
    };

  return (
  <div className="main-page">
    <Sidebar />
      <div className="portfolios-page">
         <h1>Your Portfolios</h1>
          <CreatePortfolio onPortfolioCreated={handlePortfolioCreated} />
          {error && <p className="error-message">{error}</p>}
         <PortfolioList portfolios={portfolios} onDeleteSuccess={handleDeleteSuccess} />
     </div>
    </div>
  );
};

export default PortfoliosPage;
