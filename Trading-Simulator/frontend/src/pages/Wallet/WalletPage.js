import React, { useEffect, useState } from 'react';
import AddFundsForm from '../../components/Wallet/AddFundsForm/AddFundsForm';
import BalanceDisplay from '../../components/Wallet/BalanceDisplay/BalanceDisplay';
import './WalletPage.css';
import Sidebar from '../../pages/Sidebar/Sidebar';

const WalletPage = () => {
  const [refreshBalance, setRefreshBalance] = useState(false);

  const handleFundsAdded = () => {
    setRefreshBalance(!refreshBalance);
  };

  return (
  <div className="main-page">
  <Sidebar />
  <div className="portfolio-details">
  <h1>Wallet</h1>
    <div className="wallet-page">
      <div className="balance-section">
      <div className="wallet-price">
      <h4>Current balance: </h4>
        < BalanceDisplay refresh={refreshBalance} />
        </div>
        <AddFundsForm onFundsAdded={handleFundsAdded} />
      </div>
    </div>
  </div>
    </div>
  );
};

export default WalletPage;
