import axios from 'axios';

const BASE_URL = import.meta.env.VITE_ANALYSIS_URL;

axios.defaults.withCredentials = true;

export const notebookAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'application/json',
  },
});

export default notebookAxios;