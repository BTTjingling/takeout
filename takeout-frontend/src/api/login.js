import axios from 'axios';

const baseURL = 'http://localhost:8081';

export const userLogin = (username, password) => {
  return axios.post(`/api/login/user`, {
    phone: username,
    password: password
  });
};

export const merchantLogin = (username, password) => {
  return axios.post(`${baseURL}/login/merchant`, {
    phone: username,
    password: password
  });
};

export const adminLogin = (username, password) => {
  return axios.post(`${baseURL}/login/admin`, {
    username: username,
    password: password
  });
}; 