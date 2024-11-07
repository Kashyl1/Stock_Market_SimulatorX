import axios from 'axios';

const API_URL = '/api/transactions';

export const buyAsset = async (portfolioid, currencyid, amountInUSD, amountOfCurrency) => {
  const token = localStorage.getItem('jwtToken');
  const purchaseData = { portfolioid, currencyid };

  if (amountInUSD !== null && amountInUSD !== undefined) {
    purchaseData.amountInUSD = amountInUSD;
    purchaseData.amountOfCurrency = null;
  } else if (amountOfCurrency !== null && amountOfCurrency !== undefined) {
    purchaseData.amountOfCurrency = amountOfCurrency;
    purchaseData.amountInUSD = null;
  } else {
    throw new Error('Either amountInUSD or amountOfCurrency must be provided.');
  }

  const response = await axios.post(`${API_URL}/buy-asset`, purchaseData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const sellAsset = async (portfolioid, currencyid, amount, priceInUSD) => {
  const token = localStorage.getItem('jwtToken');
  const sellData = { portfolioid, currencyid };

  if (amount !== null && amount !== undefined) {
    sellData.amount = amount;
    sellData.priceInUSD = null;
  } else if (priceInUSD !== null && priceInUSD !== undefined) {
    sellData.priceInUSD = priceInUSD;
    sellData.amount = null;
  } else {
    throw new Error('Either amount or priceInUSD must be provided.');
  }

  const response = await axios.post(`${API_URL}/sell-asset`, sellData, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
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
    throw error;
  }
};

export const getTransactionHistoryByPortfolio = async (portfolioid, params) => {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await axios.get(`${API_URL}/history/portfolio/${portfolioid}`, {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};
