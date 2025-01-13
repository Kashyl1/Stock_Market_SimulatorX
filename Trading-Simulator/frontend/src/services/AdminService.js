
import axios from 'axios';

const API_URL = '/api/admin';

export const getUsers = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/users`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const blockUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.put(
    `${API_URL}/users/${userId}/block`,
    { blocked: true },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const unblockUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.put(
    `${API_URL}/users/${userId}/block`,
    { blocked: false },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};


export const deleteUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.delete(`${API_URL}/users/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const createAdmin = async (adminData) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(`${API_URL}/users/create-admin`, adminData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });
  return response.data;
};

export const getTransactions = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/transactions`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const getTransactionsByUser = async (userId, page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/transactions/user/${userId}`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const getSuspiciousTransactions = async (thresholdAmount) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/transactions/suspicious?thresholdAmount=${thresholdAmount}`, {
    params: { thresholdAmount },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const markTransactionSuspicious = async (transactionId, suspicious) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.put(
      `${API_URL}/transactions/${transactionId}/suspicious`,
      null,
      {
        params: { suspicious },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error marking transaction suspicious:', error);
    throw error;
  }
};


export const getPortfolios = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/portfolios`, {
      params: { page, size },
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

export const deletePortfolio = async (portfolioId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.delete(`${API_URL}/portfolios/${portfolioId}`, {
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

export const getPortfolioByid = async (portfolioId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/portfolios/${portfolioId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching portfolio:', error);
    throw error;
  }
};

export const getTransactionHistoryByPortfolio = async (portfolioId, page, pageSize) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/transactions/portfolio/${portfolioId}`, {
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

export const getPortfoliosByUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/portfolios/user/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching portfolios by user:', error);
    throw error;
  }
};

export const updatePortfolioName = async (portfolioId, payload) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.put(`${API_URL}/portfolios/${portfolioId}`, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error updating portfolio name:', error);
    throw error;
  }
};

export const getAdminActions = async (page, pageSize) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/event/admin`, {
       params: { page, pageSize },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching admin actions:', error);
    throw error;
  }
};

export const deleteAdminEvent = async (eventId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.delete(`${API_URL}/event/admin/${eventId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting admin event:', error);
    throw error;
  }
};

export const getAdminStats = async () => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/event/admin/stats`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching admin stats:', error);
    throw error;
  }
};

export const getUserStats = async () => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/event/user/stats`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching user stats:', error);
    throw error;
  }
};

export const getUserEvents = async (page, pageSize) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/event/user`, {
       params: { page, pageSize },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching admin actions:', error);
    throw error;
  }
};

export const deleteUserEvent = async (eventId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.delete(`${API_URL}/event/user/${eventId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting admin event:', error);
    throw error;
  }
};

export const postGlobalAlert = async (alertData) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.post(
      `${API_URL}/global-alerts`,
      alertData,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error posting global alert:', error);
    throw error;
  }
};

export const getGlobalAlerts = async () => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.get(`${API_URL}/global-alerts`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching global alerts:', error);
    throw error;
  }
};

export const deleteGlobalAlert = async (alertId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    const response = await axios.delete(`${API_URL}/global-alerts/${alertId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting global alert:', error);
    throw error;
  }
};

export const fetchGlobalAlert = async () => {
  try {
    const token = localStorage.getItem('jwtToken');
    const response = await axios.get(`/api/global-alert`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching global alert:', error);
    throw error;
  }
};
