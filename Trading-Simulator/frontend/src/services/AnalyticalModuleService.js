import axios from 'axios';

const API_URL = '/api/analytics';
const BASE_URL = '/api/currency';

export const fetchAnalyticsData = async (indicator, currencyId, interval, periods = null) => {
  const token = localStorage.getItem('jwtToken');

  const url = (() => {
    if (indicator === 'rsi' || indicator === 'volatility') {
      return `${API_URL}/${indicator}/${currencyId}/${interval}/14`;
    } else if (indicator === 'sma' || indicator === 'ema') {
      if (!periods) {
        throw new Error(`Periods must be provided for SMA and EMA indicators.`);
      }
      return `${API_URL}/${indicator}/${currencyId}/${interval}/${periods}`;
    } else {
      return `${API_URL}/${indicator}/${currencyId}/${interval}`;
    }
  })();

  try {
    const response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    console.error('Error in fetchAnalyticsData:', error.response || error.message || error);
    throw error;
  }
};

export const fetchCurrentPrice = async (currencyId) => {
  const token = localStorage.getItem('jwtToken');
  const url = `${BASE_URL}/current-price/${currencyId}`;

  try {
    const response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return parseFloat(response.data);
  } catch (error) {
    console.error('Error in fetchCurrentPrice:', error.response || error.message || error);
    throw error;
  }
};

