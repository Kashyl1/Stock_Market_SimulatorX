/*{ In development }*/

import axios from 'axios';

const API_URL = '/api/admin/users';

export const getUsers = async (page, size) => {
  const token = localStorage.getItem('jwtToken');
  const response = await axios.get(API_URL, {
    params: { page, size },
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};
