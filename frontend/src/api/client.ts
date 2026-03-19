import axios from 'axios';

/** Gateway를 향하는 공용 Axios 인스턴스. Vite proxy가 /api → localhost:8000으로 전달한다. */
const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

/** 요청마다 localStorage의 JWT 토큰을 Authorization 헤더에 자동 주입한다. */
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/** 401 응답 시 토큰을 제거하고 로그인 페이지로 리다이렉트한다. */
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('nickname');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  },
);

export default client;
