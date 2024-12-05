import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';
import AdminPortfolios from '../../components/Admin/AdminPortfolios/AdminPortfolios';
import AdminTransactionsByUser from '../../components/Admin/AdminTransactions/AdminTransactionsByUser';
import AdminSuspiciousTransactions from '../../components/Admin/AdminTransactions/AdminSuspiciousTransactions';
import AdminPortfoliosByUser from '../../components/Admin/AdminPortfolios/AdminPortfoliosByUser';

const AdminPortfoliosPage = () => {
  const [activeButton, setActiveButton] = useState('portfolios');
  const [showPortfolios, setShowPortfolios] = useState(false);
  const [showPortfoliosByUser, setShowPortfoliosByUser] = useState(false);
  const [showSuspiciousTransactions, setShowSuspiciousTransactions] = useState(false);

const handleButtonClick = (buttonName) => {
  setActiveButton(buttonName);
  setShowPortfolios(buttonName === 'portfolios');
  setShowPortfoliosByUser(buttonName === 'portfoliosByUser');
  setShowSuspiciousTransactions(buttonName === 'SuspiciousTransactions');
};

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Portfolios Panel</h1>
        <div className="admin_button-container">
          <button
            className={`admin_action-button ${activeButton === 'portfolios' ? 'active' : ''}`}
            onClick={() => handleButtonClick('portfolios')}
          >
            Show Portfolios
          </button>
          <button
            className={`admin_action-button ${activeButton === 'portfoliosByUser' ? 'active' : ''}`}
            onClick={() => handleButtonClick('portfoliosByUser')}
          >
            Portfolios By Users
          </button>
        </div>
         {showPortfolios && <AdminPortfolios />}
         {showPortfoliosByUser && <AdminPortfoliosByUser />}
      </div>
    </div>
  );
};

export default AdminPortfoliosPage;
