import React from 'react';
import './TransactionTable.css';

const TransactionTable = ({ transactions }) => {

  return (
    <div className="assets-table">
      <div className="table-header">
        <div className="header-cell">
          Type
        </div>
        <div className="header-cell">
          Currency
        </div>
        <div className="header-cell">
          Amount
        </div>
        <div className="header-cell">
          Rate
        </div>
        <div className="header-cell">
          Date
        </div>
        <div className="header-cell">
          Portfolio
        </div>
      </div>


      <div className="table-body">
        {transactions.length === 0 ? (
          <div className="table-row">
            <div className="cell" colSpan="6">No transactions found</div>
          </div>
        ) : (
          transactions.map((tx) => (
            <div className="table-row" key={tx.transactionid}>
              <div className="cell">{tx.transactionType}</div>
              <div className="cell">{tx.currencyName}</div>
              <div className="cell">{tx.amount}</div>
              <div className="cell">${tx.rate.toFixed(2)}</div>
              <div className="cell">{new Date(tx.timestamp).toLocaleString()}</div>
              <div className="cell">{tx.portfolioName}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default TransactionTable;
