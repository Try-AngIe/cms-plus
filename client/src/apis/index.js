import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';
axios.defaults.withCredentials = true;

export const publicAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': `${BASE_URL}`,
    'Content-Type': 'application/json',
  },
});

export const privateAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': `${BASE_URL}`,
    'Content-Type': 'application/json',
    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
  },
});

export const UploadFileAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': `${BASE_URL}`,
    'Content-Type': 'multipart/form-data',
    Authorization: `Bearer ${localStorage.getItem('access_token')}`,
  },
});

export const publicUploadFileAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': `${BASE_URL}`,
    'Content-Type': 'multipart/form-data',
  },
});
