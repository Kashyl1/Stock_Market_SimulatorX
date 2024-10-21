import axios from 'axios';

const API_URL = '/api/user';

export const addFunds = async (amount) => {
  const token = localStorage.getItem('jwtToken');
  console.log('JWT Token:', token);
  const response = await axios.post(`${API_URL}/add-funds`, { amount }, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
  return response.data;
};

export const getBalance = async () => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/balance`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
  return response.data;
};

