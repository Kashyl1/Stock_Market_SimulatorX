import React, { useState } from 'react';
import './TransactionFilters.css';

const TransactionFilters = ({ filters, onFilterChange }) => {
  const handleTransactionTypeChange = (e) => {
    onFilterChange({
      ...filters,
      transactionType: e.target.value,
    });
  };

  return (
    <div className="transaction-filters">
          <label htmlFor="transactionType">Transaction type:</label>
          <select
            id="transactionType"
            value={filters.transactionType}
            onChange={handleTransactionTypeChange}
          >
            <option value="BUY">BUY</option>
            <option value="SELL">SELL</option>
          </select>
        </div>
  );
};

export default TransactionFilters;
