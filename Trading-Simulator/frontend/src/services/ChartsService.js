import axios from 'axios';

const API_URL = '/api/charts';

export const fetchChartData = async (currencyId, interval) => {
  const token = localStorage.getItem('jwtToken');

  try {
    const response = await axios.get(`${API_URL}/${currencyId}/${interval}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    console.error('Error in fetchChartData:', error.response || error.message || error);
    throw error;
  }
};
