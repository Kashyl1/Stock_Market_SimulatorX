import React, { useState, useEffect } from 'react';
import TransactionTable from './TransactionTable';
import Pagination from './Pagination';
import TransactionFilters from './TransactionFilters';
import { getTransactionHistory, getTransactionHistoryByPortfolio } from '../../services/TransactionService';
import './TransactionHistory.css';

const TransactionHistory = ({ portfolioid }) => {
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState('timestamp');
  const [sortDir, setSortDir] = useState('desc');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [filters, setFilters] = useState({
    transactionType: 'BUY',  // Ustawienie domyślnego filtra na "BUY"
  });

  useEffect(() => {
    fetchTransactions();
  }, [page, sortBy, sortDir, filters]); // fetchTransactions będzie wywoływane po zmianie page, sortu i filtrów

  const fetchTransactions = async () => {
    setLoading(true);
    setError('');
    try {
      const params = {
        page,
        size,
        sortBy,
        sortDir,
      };

      let response;
      if (portfolioid) {
        response = await getTransactionHistoryByPortfolio(portfolioid, params);
      } else {
        response = await getTransactionHistory(params);
      }

      // Filtrujemy po stronie klienta, jeśli transactionType jest ustawiony
      const filteredTransactions = filters.transactionType
        ? response.content.filter(tx => tx.transactionType === filters.transactionType)
        : response.content;

      setTransactions(filteredTransactions);
      setTotalPages(response.totalPages);
    } catch (err) {
      setError('Failed to fetch transaction history.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const handleSortChange = (newSortBy, newSortDir) => {
    setSortBy(newSortBy);
    setSortDir(newSortDir);
  };

  const handleFilterChange = (newFilters) => {
    console.log("Updated filters:", newFilters); // Logowanie w celu debugowania
    setFilters(newFilters);
    setPage(0); // Resetowanie strony przy zmianie filtra
  };

  return (
    <div className="transaction-history">
      <TransactionFilters filters={filters} onFilterChange={handleFilterChange} /> {/* Przekazanie filtrów do TransactionFilters */}

      {loading ? (
        <p>Loading transactions...</p>
      ) : error ? (
        <p className="error">{error}</p>
      ) : (
        <>
          <TransactionTable
            transactions={transactions}
            onSortChange={handleSortChange}
            sortBy={sortBy}
            sortDir={sortDir}
          />
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </>
      )}
    </div>
  );
};

export default TransactionHistory;
