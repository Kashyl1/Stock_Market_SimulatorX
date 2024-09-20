import axios from 'axios';

const API_URL = '/api/transactions';

export const getAvailableAssets = async () => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/available-assets`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};
