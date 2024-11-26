import React, { useState, useEffect } from 'react';
import { getSuspiciousTransactions } from '../../../services/AdminService';


const AdminSuspiciousTransactions = () => {
  const [thresholdAmount, setThresholdAmount] = useState(10000);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchSuspiciousTransactions = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getSuspiciousTransactions(thresholdAmount);
      setTransactions(data);
    } catch (err) {
      setError('Failed to fetch suspicious transactions.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSuspiciousTransactions();
  }, [thresholdAmount]);

  return (
    <div>
      <h2>Suspicious Transactions</h2>
      <div className="search-container">
        <label htmlFor="thresholdAmount" className="search-label">
          Threshold Amount:
        </label>
        <input
          type="number"
          id="thresholdAmount"
          value={thresholdAmount}
          onChange={(e) => setThresholdAmount(Number(e.target.value))}
          className="search-input"
        />
      </div>

      {loading ? (
        <p>Loading data...</p>
      ) : error ? (
        <p className="error">{error}</p>
      ) : transactions.length > 0 ? (
        <div className="assets-table">
          <div className="table-header_suspicious">
            <div className="header-cell">Transaction ID</div>
            <div className="header-cell">Type</div>
            <div className="header-cell">Amount</div>
            <div className="header-cell">Rate</div>
            <div className="header-cell">Currency</div>
            <div className="header-cell">User Email</div>
            <div className="header-cell">Portfolio</div>
            <div className="header-cell">Timestamp</div>
          </div>

          <div className="table-body">
            {transactions.map((transaction) => (
              <div className="table-row_admin_suspicious" key={transaction.transactionid}>
                <div className="cell">{transaction.transactionid}</div>
                <div className="cell">{transaction.transactionType}</div>
                <div className="cell">{transaction.amount.toFixed(2)}</div>
                <div className="cell">{transaction.rate}</div>
                <div className="cell">{transaction.currencyName}</div>
                <div className="cell">{transaction.userEmail}</div>
                <div className="cell">{transaction.portfolioName}</div>
                <div className="cell">
                  {new Date(transaction.timestamp).toLocaleString()}
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p>No suspicious transactions found.</p>
      )}
    </div>
  );
};

export default AdminSuspiciousTransactions;
