import { useState, useEffect } from 'react';
import { getTransactionsByUser, markTransactionSuspicious, getTransactionHistoryByPortfolio } from '../../../services/AdminService';
import '../../../components/Admin/AdminUsers/AdminUsers.css';

const UserTransactions = ({ userId, portfolioId }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [filteredTransactions, setFilteredTransactions] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [transactionPage, setTransactionPage] = useState(0);
  const [transactionPageSize] = useState(20);
  const [totalTransactionPages, setTotalTransactionPages] = useState(0);
  const [allTransactions, setAllTransactions] = useState([]);

useEffect(() => {
  setLoading(true);
  setError(null);
  const fetchTransactions = async () => {
    try {
      const allData = [];
      let currentPage = 0;
      let totalPages = 1;

      console.log('Starting to fetch transactions...');

      if (userId) {
        console.log(`Fetching transactions for userId: ${userId}`);
        while (currentPage < totalPages) {
          console.log(`Fetching page ${currentPage + 1} for userId: ${userId}`);
          const response = await getTransactionsByUser(userId, currentPage, transactionPageSize);
          console.log(`Received response for userId: ${userId}, page ${currentPage + 1}`, response);
          allData.push(...(response.content || []));
          totalPages = response.totalPages || 1;
          console.log(`Total pages for userId ${userId}: ${totalPages}`);
          currentPage += 1;
        }
      } else if (portfolioId) {
        console.log(`Fetching transactions for portfolioId: ${portfolioId}`);
        while (currentPage < totalPages) {
          console.log(`Fetching page ${currentPage + 1} for portfolioId: ${portfolioId}`);
          const response = await getTransactionHistoryByPortfolio(portfolioId, currentPage, transactionPageSize);
          console.log(`Received response for portfolioId: ${portfolioId}, page ${currentPage + 1}`, response);
          allData.push(...(response.content || []));
          totalPages = response.totalPages || 1;
          console.log(`Total pages for portfolioId ${portfolioId}: ${totalPages}`);
          currentPage += 1;
        }
      }

      console.log('All fetched transactions:', allData);

      setAllTransactions(allData);
      setTransactions(allData.slice(0, transactionPageSize));
      setFilteredTransactions(allData.slice(0, transactionPageSize));
      setTotalTransactionPages(Math.ceil(allData.length / transactionPageSize));

      console.log('Filtered transactions set:', allData.slice(0, transactionPageSize));
    } catch (err) {
      console.error('Error while fetching transactions:', err);
      setError('Error while fetching transactions.');
      setTransactions([]);
      setFilteredTransactions([]);
      setAllTransactions([]);
      setTotalTransactionPages(0);
    } finally {
      setLoading(false);
      console.log('Loading state set to false');
    }
  };

  console.log('useEffect triggered, fetching transactions...');
  fetchTransactions();
}, [userId, portfolioId, transactionPageSize]);



  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString('en-GB', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };

  const handleSearch = (query) => {
    setSearchQuery(query);
    if (query.trim() === '') {
      const start = transactionPage * transactionPageSize;
      const end = start + transactionPageSize;
      setFilteredTransactions(allTransactions.slice(start, end));
    } else {
      const filtered = allTransactions.filter((transaction) =>
        transaction.transactionid.toString().includes(query)
      );
      setFilteredTransactions(filtered);
    }
  };

  const handleNextTransactionPage = () => {
    if (searchQuery.trim() === '') {
      if (transactionPage < totalTransactionPages - 1) {
        setTransactionPage((prev) => prev + 1);
        const start = (transactionPage + 1) * transactionPageSize;
        const end = start + transactionPageSize;
        setFilteredTransactions(allTransactions.slice(start, end));
      }
    }
  };

  const handlePrevTransactionPage = () => {
    if (searchQuery.trim() === '') {
      if (transactionPage > 0) {
        setTransactionPage((prev) => prev - 1);
        const start = (transactionPage - 1) * transactionPageSize;
        const end = start + transactionPageSize;
        setFilteredTransactions(allTransactions.slice(start, end));
      }
    }
  };

  const handleMarkSuspicious = async (transactionId, currentStatus) => {
    try {
      const updatedTransaction = await markTransactionSuspicious(transactionId, !currentStatus);
      setTransactions((prev) =>
        prev.map((transaction) =>
          transaction.transactionid === transactionId
            ? { ...transaction, suspicious: !currentStatus }
            : transaction
        )
      );
      setFilteredTransactions((prev) =>
        prev.map((transaction) =>
          transaction.transactionid === transactionId
            ? { ...transaction, suspicious: !currentStatus }
            : transaction
        )
      );
    } catch (err) {
      setError('Error updating transaction status.');
      console.error(err);
    }
  };

  if (loading) return <p>Loading transactions...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div>
      <h3>{userId ? 'User Transactions' : 'Portfolio Transactions'}</h3>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by Transaction ID"
          value={searchQuery}
          onChange={(e) => handleSearch(e.target.value)}
          className="search-input"
        />
      </div>
      {filteredTransactions.length > 0 ? (
        <div className="transactions-table">
          <div className="table-header_admin_userTransactions">
            <div className="header-cell">Transaction ID</div>
            <div className="header-cell">Portfolio ID</div>
            <div className="header-cell">Type</div>
            <div className="header-cell">Currency</div>
            <div className="header-cell">Amount</div>
            <div className="header-cell">Rate</div>
            <div className="header-cell">Total Price</div>
            <div className="header-cell">Timestamp</div>
            <div className="header-cell">Suspicious</div>
            <div className="header-cell">Actions</div>
          </div>
          <div className="table-body">
            {filteredTransactions.map((transaction) => (
              <div className="table-row_admin_userTransactions" key={transaction.transactionid}>
                <div className="cell">{transaction.transactionid}</div>
                <div className="cell">{transaction.portfolioid}</div>
                <div className="cell">{transaction.transactionType}</div>
                <div className="cell">{transaction.currencyName}</div>
                <div className="cell">{transaction.amount}</div>
                <div className="cell">{transaction.rate}</div>
                <div className="cell">{transaction.totalPrice.toFixed(2)}</div>
                <div className="cell">{formatTimestamp(transaction.timestamp)}</div>
                <div className="cell">{transaction.suspicious ? 'Yes' : 'No'}</div>
                <div className="cell">
                  <button
                    onClick={() =>
                      handleMarkSuspicious(transaction.transactionid, transaction.suspicious)
                    }
                    disabled={transaction.suspicious}
                    className={transaction.suspicious ? 'btn-disabled' : 'btn-active'}
                  >
                    {transaction.suspicious ? 'Mark as Unsuspicious' : 'Mark as Suspicious'}
                  </button>
                </div>
              </div>
            ))}
          </div>

        </div>
      ) : (
        <p>No transactions found.</p>
      )}
      <div className="pagination-controls">
        <button onClick={handlePrevTransactionPage} disabled={transactionPage === 0}>
          Previous
        </button>
        <span>
          Page {transactionPage + 1} of {totalTransactionPages}
        </span>
        <button
          onClick={handleNextTransactionPage}
          disabled={transactionPage === totalTransactionPages - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default UserTransactions;
