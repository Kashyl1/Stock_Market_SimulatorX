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
    <div className="wallet-page">
      <div className="balance-section">
      <h1>Your Balance</h1>
        <BalanceDisplay refresh={refreshBalance} />
        <AddFundsForm onFundsAdded={handleFundsAdded} />
      </div>
    </div>
  </div>
  );
};

export default WalletPage;
