import React, { useEffect, useState } from 'react';
import { deactivateTradeAlert, deleteTradeAlert } from '../../../services/TradeAlertService';
import { getPortfolioByid } from '../../../services/PortfolioService';
import './TradeAlertItem.css';
import { Notyf } from 'notyf';
import 'notyf/notyf.min.css';


const notyf = new Notyf({
  ripple: false,
});

const TradeAlertItem = ({ tradeAlertData, onTradeAlertDeactivated, onTradeAlertDeleted }) => {
  const [portfolioName, setPortfolioName] = useState('Loading...');


  useEffect(() => {
    const fetchPortfolioName = async () => {
      try {
        const portfolio = await getPortfolioByid(tradeAlertData.portfolioId);
        setPortfolioName(portfolio.name);
      } catch (error) {
        console.error('Failed to fetch portfolio name:', error);
        setPortfolioName('Unknown');
      }
    };

    if (tradeAlertData.portfolioId) {
      fetchPortfolioName();
    }
  }, [tradeAlertData.portfolioId]);

const handleDeactivate = async () => {
  try {
    await deactivateTradeAlert(tradeAlertData.tradeAlertId);
    notyf.success('Trade order has been deactivated.');
    onTradeAlertDeactivated(tradeAlertData.tradeAlertId);
  } catch (err) {
    const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
    notyf.error(`Failed to deactivate trade alert. ${errorMessage}`);
  }
};

const handleDelete = async () => {
  try {
    await deleteTradeAlert(tradeAlertData.tradeAlertId);
    notyf.success('Trade order has been deleted.');
    onTradeAlertDeleted(tradeAlertData.tradeAlertId);
  } catch (err) {
    const errorMessage = err.response?.data?.message || 'An unexpected error occurred.';
    notyf.error(`Failed to delete trade alert. ${errorMessage}`);
  }
};

  return (
    <div className="table-row_trade">
      <div className="cell">{tradeAlertData.currencyName}</div>
       <div className="cell">{portfolioName}</div>
      <div className="cell">
        {tradeAlertData.initialPrice ? `$${tradeAlertData.initialPrice.toFixed(2)}` : 'N/A'}
      </div>
      <div className="cell">{tradeAlertData.tradeAlertType}</div>
      <div className="cell">
        {tradeAlertData.conditionPrice ? `$${tradeAlertData.conditionPrice.toFixed(2)}` : 'N/A'}
      </div>
      <div className="cell">
        {tradeAlertData.tradeAlertType === 'BUY'
          ? `$${tradeAlertData.tradeAmount.toFixed(2)}`
          : `${tradeAlertData.tradeAmount.toFixed(8)} units`}
      </div>
      <div className="cell">
        {tradeAlertData.active ? (
          <button onClick={handleDeactivate} className="deactivate-button">
            Deactivate
          </button>
        ) : (
          <button onClick={handleDelete} className="delete-button">
            Delete
          </button>
        )}
      </div>
      <div className="cell">{tradeAlertData.active ? 'Active' : 'Triggered'}</div>
    </div>
  );
};

export default TradeAlertItem;
