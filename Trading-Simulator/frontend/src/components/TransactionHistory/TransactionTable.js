import React from 'react';
import './TransactionTable.css';

const TransactionTable = ({ transactions, onSortChange, sortBy, sortDir }) => {

  const handleSort = (column) => {
    if (sortBy === column) {
      onSortChange(column, sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      onSortChange(column, 'desc');
    }
  };

  const renderSortIcon = (column) => {
    if (sortBy !== column) return null;
    return sortDir === 'asc' ? ' 🔼' : ' 🔽';
  };

  return (
    <table className="transaction-table">
      <thead>
        <tr>
          <th onClick={() => handleSort('transactionType')}>Type{renderSortIcon('transactionType')}</th>
          <th onClick={() => handleSort('currencyName')}>Currency{renderSortIcon('currencyName')}</th>
          <th onClick={() => handleSort('amount')}>Amount{renderSortIcon('amount')}</th>
          <th onClick={() => handleSort('rate')}>Rate{renderSortIcon('rate')}</th>
          <th onClick={() => handleSort('timestamp')}>Date{renderSortIcon('timestamp')}</th>
          <th onClick={() => handleSort('portfolioName')}>Portfolio{renderSortIcon('portfolioName')}</th>
        </tr>
      </thead>
      <tbody>
        {transactions.length === 0 ? (
          <tr>
            <td colSpan="6">No transactions found.</td>
          </tr>
        ) : (
          transactions.map(tx => (
            <tr key={tx.transactionid}>
              <td>{tx.transactionType}</td>
              <td>{tx.currencyName}</td>
              <td>{tx.amount}</td>
              <td>${tx.rate.toFixed(2)}</td>
              <td>{new Date(tx.timestamp).toLocaleString()}</td>
              <td>{tx.portfolioName}</td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
};

export default TransactionTable;
