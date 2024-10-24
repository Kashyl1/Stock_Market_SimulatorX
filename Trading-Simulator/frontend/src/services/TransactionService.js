import axios from 'axios';

const API_URL = '/api/transactions';

export const buyAsset = async (portfolioid, currencyid, amountInUSD) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/buy-asset`,
    { portfolioid, currencyid, amountInUSD },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const sellAsset = async (portfolioid, currencyid, amount) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/sell-asset`,
    { portfolioid, currencyid, amount },
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
