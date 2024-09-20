import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

const register = (firstname, lastname, email, password) => {
    return axios.post(`${API_URL}/register`, {
        firstname,
        lastname,
        email,
        password,
    });
};

const login = (email, password) => {
    return axios.post(`${API_URL}/authenticate`, {
        email,
        password,
    });
};

export default {
    register,
    login,
};
