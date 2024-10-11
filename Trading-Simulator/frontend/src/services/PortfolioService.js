// src/services/PortfolioService.js

import axios from 'axios';

const API_URL = '/api/portfolios';

export const createPortfolio = async (name) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/create`,
    { name },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const getUserPortfolios = async () => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/my-portfolios`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching portfolios:', error);
    throw error;
  }
};

export const getPortfolioById = async (id) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching portfolio by ID:', error);
    throw error;
  }
};

export const getPortfolioAssetsWithGains = async (portfolioId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/${portfolioId}/gains`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    throw error;
  }
};

export const getTotalPortfolioGainOrLoss = async (portfolioId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/${portfolioId}/total-gain-or-loss`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};
