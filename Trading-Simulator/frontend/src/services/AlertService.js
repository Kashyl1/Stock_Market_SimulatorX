import axios from 'axios';

const API_URL = '/api/alerts';

export const createAlert = async (alertData) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/create`,
    alertData,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const getUserAlerts = async () => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/my-alerts`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const deactivateAlert = async (alertId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/deactivate/${alertId}`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const deleteAlert = async (alertId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.delete(
    `${API_URL}/${alertId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};
