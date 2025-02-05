import React, { useState, useEffect, useCallback } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExchangeAlt } from '@fortawesome/free-solid-svg-icons';
import { getTransactionsTodayCount } from '../../../services/MainPageService';


const CountCard = () => {
  const [transactionCount, setTransactionCount] = useState(null);
  const [refresh, setRefresh] = useState(false);

  const fetchTransactionCount = useCallback(async () => {
    try {
      const count = await getTransactionsTodayCount();
      setTransactionCount(count);
    } catch (error) {
      console.error('Failed to fetch transaction count:', error);
    }
  }, []);

  useEffect(() => {
    fetchTransactionCount();
  }, [fetchTransactionCount, refresh]);

  const handleReload = () => {
    setRefresh((prev) => !prev);
  };

  return (
    <div className="transaction-card">
      <div className="transaction-icon">
        <FontAwesomeIcon icon={faExchangeAlt} size="2x" color="gold" />
      </div>
      <div className="transaction-content">
        <div className="transaction-header">
          <div className="header-row">
            <span className="title">Today's Transactions</span>
          </div>
        </div>
        <div className="transaction-value">
          <span className="count">
            {transactionCount !== null ? transactionCount : "Loading..."}
          </span>
        </div>
      </div>
    </div>
  );
};

export default CountCard;
