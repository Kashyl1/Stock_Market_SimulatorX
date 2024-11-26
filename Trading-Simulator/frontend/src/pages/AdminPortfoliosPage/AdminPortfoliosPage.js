import { useState } from 'react';
import AdminSidebar from '../../components/Admin/AdminSidebar';

const AdminPortfoliosPage = () => {
  const [activeButton, setActiveButton] = useState('portfolios');

const handleButtonClick = (buttonName) => {
  setActiveButton(buttonName);

};

  return (
    <div className="main-page">
      <AdminSidebar />
      <div className="portfolio-details">
        <h1>Portfolios Panel</h1>
        <div className="admin_button-container">
          <button
            className={`action-button ${activeButton === 'Transactions' ? 'active' : ''}`}
            onClick={() => handleButtonClick('portfolios')}
          >
            Show Portfolios
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminPortfoliosPage;
