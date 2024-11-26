/*{ In development }*/

import axios from 'axios';

const API_URL = '/api/admin';

export const getUsers = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/users`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const blockUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.put(
    `${API_URL}/users/${userId}/block`,
    { blocked: true },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

export const unblockUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.put(
    `${API_URL}/users/${userId}/block`,
    { blocked: false },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};


export const deleteUser = async (userId) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.delete(`${API_URL}/users/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const createAdmin = async (adminData) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.post(`${API_URL}/users/create-admin`, adminData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });
  return response.data;
};

export const getTransactions = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/transactions`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const getTransactionsByUser = async (userId, page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/transactions/user/${userId}`, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const getSuspiciousTransactions = async (thresholdAmount) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(`${API_URL}/transactions/suspicious?thresholdAmount=${thresholdAmount}`, {
    params: { thresholdAmount },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

export const markTransactionSuspicious = async (transactionId, suspicious) => {
  const response = await axios.put(
    `${API_URL}/transactions/${transactionId}/suspicious?suspicious=${suspicious}`,
    null,
    {
      params: { suspicious },
    }
  );
  return response.data;
};
