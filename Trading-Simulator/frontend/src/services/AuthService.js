import axios from 'axios';

const API_URL = '/api/auth';

export const register = async (firstname, lastname, email, password) => {
  try {
    const response = await axios.post(`${API_URL}/register`, {
      firstname,
      lastname,
      email,
      password,
    });
    return response.data;
  } catch (error) {
    console.error('Error during registration:', error);
    throw error;
  }
};

export const login = async (email, password) => {
  try {
    const response = await axios.post(`${API_URL}/authenticate`, {
      email,
      password,
    });
    if (response.data.token) {
      localStorage.setItem('jwtToken', response.data.token);
    }
    return response.data;
  } catch (error) {
    console.error('Error during login:', error);
    throw error;
  }
};

export const verifyAccount = async (token) => {
  try {
    const response = await axios.get(`${API_URL}/verify`, {
      params: { token },
    });
    return response.data;
  } catch (error) {
    console.error('Error during account verification:', error);
    throw error;
  }
};

export const resendVerificationEmail = async (email) => {
  try {
    const response = await axios.post(`${API_URL}/resend-verification`, { email });
    return response.data;
  } catch (error) {
    console.error('Error during resend verification email:', error);
    throw error;
  }
};
