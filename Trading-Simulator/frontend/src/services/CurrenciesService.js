import axios from 'axios';

const API_URL = '/api/transactions';

export const getAvailableAssets = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/available-assets`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};
