import React, { useState, useEffect, useCallback } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faWallet } from '@fortawesome/free-solid-svg-icons';
import { useParams } from 'react-router-dom';
import { getGlobalGain } from '../../../services/PortfolioService';
import BalanceDisplay from './BalanceDisplay';
import './BalanceDisplay.css';

const WalletCard = () => {
  const { id } = useParams();
  const [globalGain, setGlobalGain] = useState(null);
  const [balanceValue, setBalanceValue] = useState(0);
  const [refresh, setRefresh] = useState(false);

  const fetchGlobalGain = useCallback(async () => {
    try {
      const gain = await getGlobalGain();
      setGlobalGain(gain);
    } catch (error) {
      console.error('Failed to fetch global gain:', error);
    }
  }, []);

  useEffect(() => {
    fetchGlobalGain();
  }, [fetchGlobalGain, refresh]);

  const gainColor = globalGain > 0 ? "green" : "red";
  const formattedGain = globalGain !== null ?
    `${globalGain > 0 ? "+" : ""}$${parseFloat(globalGain).toFixed(2)}` :
    "Loading...";


  const totalSum = (globalGain || 0) + (balanceValue || 0);


  const handleReload = () => {
    setRefresh((prev) => !prev);
  };

  return (
    <div className="wallet-card">

      <div className="wallet-icon">
        <FontAwesomeIcon icon={faWallet} size="2x" color="green" />
      </div>
      <div className="wallet-content">
        <div className="wallet-header">
          <div className="header-row">
            <span className="title">Total (Gain + Balance)</span>
            <span className="right-title">Balance</span>
          </div>
        </div>
        <div className="wallet-value">
          <span className="gain" style={{ color: gainColor }}>{formattedGain}</span>
          <span className="balance">
            <BalanceDisplay refresh={refresh} onBalanceUpdate={setBalanceValue} />
          </span>
        </div>
        <div className="wallet-total">
          <span style={{ color: "gold", fontWeight: "bold", fontSize: "1.5em" }}>
            ${totalSum.toFixed(2)}
          </span>
        </div>
        <div className="wallet-footer">
          <span>(<a href="#" onClick={handleReload}>Press to reload</a>)</span>
        </div>
      </div>
    </div>
  );

};

export default WalletCard;
