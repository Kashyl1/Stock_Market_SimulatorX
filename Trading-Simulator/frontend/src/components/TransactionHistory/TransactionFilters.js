import React, { useState } from 'react';
import './TransactionFilters.css';

const TransactionFilters = ({ onFilterChange }) => {
  const [transactionType, setTransactionType] = useState('');

  const handleTypeChange = (e) => {
    setTransactionType(e.target.value);
    onFilterChange({ transactionType: e.target.value });
  };

  return (
    <div className="transaction-filters">
      <label>
        Transaction Type:
        <select value={transactionType} onChange={handleTypeChange}>
          <option value="">All</option>
          <option value="BUY">Buy</option>
          <option value="SELL">Sell</option>
        </select>
      </label>
    </div>
  );
};

export default TransactionFilters;
