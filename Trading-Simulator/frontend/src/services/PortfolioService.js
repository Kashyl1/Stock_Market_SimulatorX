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
    throw error;
  }
};

export const getPortfolioByid = async (id) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const getPortfolioAssetsWithGains = async (portfolioid) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/${portfolioid}/gains`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    throw error;
  }
};

export const getTotalPortfolioGainOrLoss = async (portfolioid) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/${portfolioid}/total-gain-or-loss`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const deletePortfolio = async (portfolioid) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.delete(`${API_URL}/${portfolioid}/delete-portfolio`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting portfolio:', error);
    throw error;
  }
};