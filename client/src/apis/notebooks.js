import axios from 'axios';

export const notebookAxios = axios.create({
  baseURL: 'http://localhost:8000',
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'application/json',
  },
});

export default notebookAxios;