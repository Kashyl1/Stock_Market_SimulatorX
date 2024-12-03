import React, { useState, useEffect } from 'react';
import { getTransactions } from '../../../services/AdminService';
import './AdminTransactions.css';

const AdminSuspiciousTransactions = () => {
  const [suspiciousTransactions, setSuspiciousTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchSuspiciousTransactions = async () => {
    setLoading(true);
    setError(null);
    try {
      let allSuspicious = [];
      let currentPage = 0;
      let response;

      do {
        response = await getTransactions(currentPage, 100);
        const filteredSuspicious = response.content.filter(txn => txn.suspicious === true);
        allSuspicious = [...allSuspicious, ...filteredSuspicious];
        currentPage++;
      } while (!response.last);

      setSuspiciousTransactions(allSuspicious);
    } catch (err) {
      setError('Error while fetching suspicious transactions.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSuspiciousTransactions();
  }, []);

  if (loading) return <p>Loading suspicious transactions...</p>;
  if (error) return <p className="txn-error">{error}</p>;

  return (
    <div className="txn-magic-container">
      <h2 className="txn-glow-title">Suspicious Transactions</h2>
      {suspiciousTransactions.length > 0 ? (
        <div className="txn-dimension-table">
          <div className="txn-portal-header">
            <div className="txn-header-cell-unique">ID</div>
            <div className="txn-header-cell-unique">Portfolio ID</div>
            <div className="txn-header-cell-unique">Type</div>
            <div className="txn-header-cell-unique">Amount</div>
            <div className="txn-header-cell-unique">Rate</div>
            <div className="txn-header-cell-unique">Currency</div>
            <div className="txn-header-cell-unique">Portfolio</div>
            <div className="txn-header-cell-unique">Suspicious</div>
            <div className="txn-header-cell-unique">User Email</div>
            <div className="txn-header-cell-unique">Total Price</div>
          </div>

          <div className="txn-portal-body">
            {suspiciousTransactions.map(transaction => (
              <div className="txn-universe-row" key={transaction.transactionid}>
                <div className="txn-cell-dimension">{transaction.transactionid}</div>
                <div className="txn-cell-dimension">{transaction.portfolioid}</div>
                <div className="txn-cell-dimension">{transaction.transactionType}</div>
                <div className="txn-cell-dimension">{transaction.amount}</div>
                <div className="txn-cell-dimension">{transaction.rate}</div>
                <div className="txn-cell-dimension">{transaction.currencyName}</div>
                <div className="txn-cell-dimension">{transaction.portfolioName}</div>
                <div className="txn-cell-dimension">{transaction.suspicious ? 'Yes' : 'No'}</div>
                <div className="txn-cell-dimension">{transaction.userEmail}</div>
                <div className="txn-cell-dimension">{transaction.totalPrice.toFixed(2)}</div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p>No suspicious transactions available.</p>
      )}
    </div>
  );
};

export default AdminSuspiciousTransactions;
