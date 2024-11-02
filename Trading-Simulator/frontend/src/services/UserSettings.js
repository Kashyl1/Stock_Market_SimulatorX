import axios from 'axios';

const API_URL = '/api/user-settings';

export const changePassword = async (currentPassword, newPassword) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/change-password`,
    { currentPassword, newPassword },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const deleteAccount = async (confirmText) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/delete-account`,
    { confirmText },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const changeEmail = async (currentPassword, newEmail) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(
    `${API_URL}/change-email`,
    { currentPassword, newEmail },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};