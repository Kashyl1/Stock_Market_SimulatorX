import React, { useEffect, useState } from 'react';
import { getBalance } from '../../services/WalletService';

const BalanceDisplay = () => {
  const [balance, setBalance] = useState(null);  // Domyślnie null
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
  }, []);

  if (error) {
    return <p className="error-message">{error}</p>;
  }

  return (
    <div className="balance-display">
      <h3>Your Balance</h3>
      {balance !== null && balance !== undefined ? (
        <p>${balance.toFixed(2)}</p>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
};

export default BalanceDisplay;
