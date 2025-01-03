import React, { useEffect, useState } from 'react';
import { getBalance } from '../../../services/WalletService';

const BalanceDisplay = ({ refresh }) => {
  const [balance, setBalance] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const data = await getBalance();
        setBalance(data.balance);
      } catch (error) {
        setError('Failed to fetch balance. Please try again later.');
      }
    };

    fetchBalance();
  }, [refresh]);

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  return (
    <div className="balance-display">
      {balance !== null && balance !== undefined ? (
        <p>${balance.toFixed(2)}</p>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
};

export default BalanceDisplay;
