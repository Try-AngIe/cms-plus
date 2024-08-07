import axios from 'axios';
import { postRefreshToken } from './auth';

const BASE_URL = import.meta.env.VITE_BASE_URL;
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

// <----- 응답 인터셉터 설정 ----->
[publicAxios, publicUploadFileAxios, privateAxios].forEach(instance => {
  instance.interceptors.response.use(
    response => response,
    err => {
      if (err.response) {
        console.log(err.response.status);

        switch (err.response.status) {
          case 403:
            window.location.href = '/error/forbidden';
            break;
          case 404:
            window.location.href = '/error/notfound';
            break;
          case 500:
            window.location.href = '/error/internal';
            break;
          default:
            break;
        }
      }
      return Promise.reject(err);
    }
  );
});

// <----- 요청 인터셉터 설정 ----->
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

// <----- 토큰 재발급 인터셉터 설정 ----->
privateAxios.interceptors.response.use(
  response => {
    return response;
  },
  async err => {
    const { config } = err;
    const originRequest = config;
    if (err.response && err.response.status === 401) {
      try {
        console.log('access_token 만료');
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
