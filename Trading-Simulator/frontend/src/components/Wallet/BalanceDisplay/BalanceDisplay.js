import React, { useEffect, useState } from 'react';
import { getBalance } from '../../../services/WalletService';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';

const notyf = new Notyf({
  ripple: false,
});

const BalanceDisplay = ({ onBalanceUpdate }) => {
  const [balance, setBalance] = useState(null);

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const data = await getBalance();
        setBalance(data.balance);
        if (onBalanceUpdate) {
          onBalanceUpdate(data.balance);
        }
      } catch (error) {
        notyf.error('Failed to fetch balance. Please try again later.');
      }
    };

    fetchBalance();
  }, [onBalanceUpdate]);

  return (
    <div>
      {balance !== null && balance !== undefined ? (
        <>${balance.toFixed(2)}</>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
};

export default BalanceDisplay;
