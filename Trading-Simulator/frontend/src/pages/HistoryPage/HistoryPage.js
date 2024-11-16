import { useState, useEffect } from 'react';
import { getTransactionHistory } from '../../services/TransactionService';
import Sidebar from '../../pages/Sidebar/Sidebar';

const HistoryPage = () => {
  const [transactionHistory, setTransactionHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyError, setHistoryError] = useState(null);

  const fetchTransactionHistory = async () => {
    setHistoryLoading(true);
    try {
      const response = await getTransactionHistory();

      if (response.content && Array.isArray(response.content)) {
        setTransactionHistory(response.content);
      } else {
        console.error('No transactions in response content.');
      }
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch transaction history.';
      setHistoryError(errorMessage);
    } finally {
      setHistoryLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactionHistory();
  }, []);

  return (
    <div className="main-page">
      <Sidebar />
      <div className="portfolio-details">
        <h1>Full History</h1>
        {historyLoading && <p>Loading...</p>}
        {historyError && <p className="error">{historyError}</p>}

        {transactionHistory.length > 0 ? (
          <div className="assets-table">
            <div className="table-header">
              <div className="header-cell">Type</div>
              <div className="header-cell">Currency</div>
              <div className="header-cell">Amount</div>
              <div className="header-cell">Rate</div>
              <div className="header-cell">Date</div>
              <div className="header-cell">Portfolio</div>
            </div>

            <div className="table-body">
              {transactionHistory.map((transaction, index) => (
                <div className="table-row" key={index}>
                  <div className="cell">{transaction.transactionType}</div>
                  <div className="cell">{transaction.currencyName}</div>
                  <div className="cell">{transaction.amount}</div>
                  <div className="cell">${transaction.rate.toFixed(2)}</div>
                  <div className="cell">{new Date(transaction.timestamp).toLocaleString()}</div>
                  <div className="cell">{transaction.portfolioName}</div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <p>No transaction history available.</p>
        )}
      </div>
    </div>
  );
};

export default HistoryPage;
