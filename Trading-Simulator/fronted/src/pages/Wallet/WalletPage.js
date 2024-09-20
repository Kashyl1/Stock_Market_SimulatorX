import React, { useEffect } from 'react';
import AddFundsForm from '../../components/Wallet/AddFundsForm';
import BalanceDisplay from '../../components/Wallet/BalanceDisplay';
import './WalletPage.css';

const WalletPage = () => {

  const handleFundsAdded = () => {
    window.location.reload();
  };

  return (
    <div className="wallet-page">
      <div className="balance-section">
        <BalanceDisplay />
        <AddFundsForm onFundsAdded={handleFundsAdded} />
        {/* O tutaj jeszcze potem wypłata pieniedzy na konto no nie ale to w serwisie najpierw i backendzik*/}
        {/* Dobra */}
      </div>
      <canvas className="background"></canvas>
    </div>
  );
};

export default WalletPage;
