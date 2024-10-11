// src/services/TransactionService.js

import axios from 'axios';

const API_URL = '/api/transactions';

export const buyAsset = async (portfolioID, currencyID, amountInUSD) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/buy-asset`,
    { portfolioID, currencyID, amountInUSD },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const sellAsset = async (portfolioID, currencyID, amount) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/sell-asset`,
    { portfolioID, currencyID, amount },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const getTransactionHistory = async (params) => {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await axios.get(`${API_URL}/history`, {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching transaction history:', error);
    throw error;
  }
};

export const getTransactionHistoryByPortfolio = async (portfolioId, params) => {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await axios.get(`${API_URL}/history/portfolio/${portfolioId}`, {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching transaction history by portfolio:', error);
    throw error;
  }
};
