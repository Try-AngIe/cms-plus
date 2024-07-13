import axios from 'axios';
import { postRefreshToken } from './auth';

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

export const publicUploadFileAxios = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': `${BASE_URL}`,
    'Content-Type': 'multipart/form-data',
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

// 요청 인터셉터 설정
privateAxios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 설정
privateAxios.interceptors.response.use(
  response => {
    return response;
  },
  async err => {
    console.log('access_token 만료');
    const { config } = err;
    const originRequest = config;
    if (err.response && err.response.status === 401) {
      try {
        const response = await postRefreshToken();
        const newAccessToken = response.data.accessToken;

        // 원래 요청을 위한 값 셋팅
        localStorage.setItem('access_token', newAccessToken);

        axios.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
        originRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

        // 원래 요청 재시도
        console.log('리프레시 토큰 재발급 완료');
        return axios(originRequest);
      } catch (err) {
        console.error('리프레시 토큰 요청 실패:', err);
        localStorage.removeItem('access_token');
        window.location.href = '/';
      }
    }
    return Promise.reject(err);
  }
);
