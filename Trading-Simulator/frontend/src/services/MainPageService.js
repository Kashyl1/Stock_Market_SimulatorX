import axios from 'axios';
const API_URL = '/api/event/user/transactions/today/count';

export const getTransactionsTodayCount = async () => {
  const token = localStorage.getItem('jwtToken');

  try {
    const response = await axios.get(API_URL, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    console.error('Error fetching transactions count:', error);
    throw error;
  }
};
