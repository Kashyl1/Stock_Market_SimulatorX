import axios from 'axios';

const API_URL = '/api/alerts/trade';

const getAuthHeaders = () => {
  const token = localStorage.getItem('jwtToken');
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

export const createTradeAlert = async (tradeAlertData) => {
  const response = await axios.post(`${API_URL}/create`, tradeAlertData, getAuthHeaders());
  return response.data;
};

export const getUserTradeAlerts = async () => {
  const response = await axios.get(`${API_URL}/my-trade-alerts`, getAuthHeaders());
  return response.data;
};

export const deactivateTradeAlert = async (tradeAlertId) => {
  const response = await axios.put(
    `${API_URL}/deactivate/${tradeAlertId}`,
    {},
    getAuthHeaders()
  );
  return response.data;
};

export const deleteTradeAlert = async (tradeAlertId) => {
  const response = await axios.delete(`${API_URL}/${tradeAlertId}`, getAuthHeaders());
  return response.data;
};
