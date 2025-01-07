import axios from 'axios';

const API_URL = '/api/analytics';

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
    console.log(url);
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


