import React, { useState, useEffect } from 'react';
import { getTransactions, markTransactionSuspicious } from '../../../services/AdminService';
import './AdminTransactions.css';

const AdminTransactionsUnique = () => {
  const [transactions, setTransactions] = useState([]);
  const [allTransactions, setAllTransactions] = useState([]); // Przechowuje wszystkie transakcje dla wyszukiwania
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchTransactions = async (page) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getTransactions(page, 20);
      setTransactions(data.content);
      setTotalPages(data.totalPages);
      if (page === 0) {
        // Przy pierwszym ładowaniu pobierz wszystkie transakcje
        let allData = [];
        let currentPage = 0;
        let response;
        do {
          response = await getTransactions(currentPage, 100); // Większa liczba na stronę, żeby szybciej zebrać dane
          allData = [...allData, ...response.content];
          currentPage++;
        } while (!response.last);
        setAllTransactions(allData);
      }
    } catch (err) {
      setError('Error while fetching transactions.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions(page);
  }, [page]);

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
    if (e.target.value === '') {
      setTransactions(allTransactions.slice(page * 20, (page + 1) * 20)); // Przywróć widok paginacji
    } else {
      const filteredTransactions = allTransactions.filter(transaction =>
        transaction.transactionid.toString().includes(e.target.value)
      );
      setTransactions(filteredTransactions);
    }
  };

  const handleMarkSuspicious = async (transactionId, currentStatus) => {
    try {
      const updatedTransaction = await markTransactionSuspicious(transactionId, !currentStatus);
      setTransactions(prev =>
        prev.map(transaction =>
          transaction.transactionid === transactionId
            ? { ...transaction, suspicious: !currentStatus }
            : transaction
        )
      );
      setAllTransactions(prev =>
        prev.map(transaction =>
          transaction.transactionid === transactionId
            ? { ...transaction, suspicious: !currentStatus }
            : transaction
        )
      );
    } catch (err) {
      setError('Error updating transaction status.');
    }
  };

  const handlePrevPage = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  if (loading) return <p>Loading transactions...</p>;
  if (error) return <p className="txn-error">{error}</p>;

  return (
    <div className="txn-magic-container">
      <h2 className="txn-glow-title">History of All Transactions</h2>

      <div className="search-container">
        <input
          type="text"
          placeholder="Search by Transaction ID"
          value={searchTerm}
          onChange={handleSearch}
          className="search-input"
        />
      </div>

      {transactions.length > 0 ? (
        <div className="scroll-container">
          <div className="txn-dimension-table">
            <div className="txn-portal-header">
              <div className="txn-header-cell-unique">ID</div>
              <div className="txn-header-cell-unique">Type</div>
              <div className="txn-header-cell-unique">Amount</div>
              <div className="txn-header-cell-unique">Rate</div>
              <div className="txn-header-cell-unique">Currency</div>
              <div className="txn-header-cell-unique">Portfolio</div>
              <div className="txn-header-cell-unique">Suspicious</div>
              <div className="txn-header-cell-unique">User Email</div>
              <div className="txn-header-cell-unique">Total Price</div>
              <div className="txn-header-cell-unique">Actions</div>
            </div>

            <div className="txn-portal-body">
              {transactions.map(transaction => (
                <div className="txn-universe-row" key={transaction.transactionid}>
                  <div className="txn-cell-dimension">{transaction.transactionid}</div>
                  <div className="txn-cell-dimension">{transaction.transactionType}</div>
                  <div className="txn-cell-dimension">{transaction.amount}</div>
                  <div className="txn-cell-dimension">{transaction.rate}</div>
                  <div className="txn-cell-dimension">{transaction.currencyName}</div>
                  <div className="txn-cell-dimension">{transaction.portfolioName}</div>
                  <div className="txn-cell-dimension">{transaction.suspicious ? 'Yes' : 'No'}</div>
                  <div className="txn-cell-dimension">{transaction.userEmail}</div>
                  <div className="txn-cell-dimension">{transaction.totalPrice.toFixed(2)}</div>
                  <div className="cell">
                    <button
                      onClick={() =>
                        handleMarkSuspicious(transaction.transactionid, transaction.suspicious)
                      }
                    >
                      {transaction.suspicious ? 'Mark as Unsuspicious' : 'Mark as Suspicious'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      ) : (
        <p>No transactions available.</p>
      )}

      <div className="pagination-controls">
        <button onClick={handlePrevPage} disabled={page === 0}>
          Previous
        </button>
        <span>
          Page {page + 1} of {totalPages}
        </span>
        <button onClick={handleNextPage} disabled={page === totalPages - 1}>
          Next
        </button>
      </div>
    </div>

  );
};

export default AdminTransactionsUnique;
