import { useState, type FormEvent } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import client from '../api/client';
import styles from './AuthForm.module.css';

/** 로그인 페이지. 이메일·비밀번호를 입력받아 JWT 토큰을 발급받는다. */
function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const message = (location.state as { message?: string })?.message;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      const { data } = await client.post<{ token: string; nickname: string }>(
        '/users/login',
        { email, password },
      );
      login(data.token, data.nickname);
      navigate('/');
    } catch (err: unknown) {
      if (isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('로그인에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>로그인</h2>
      <form onSubmit={handleSubmit} className={styles.form}>
        {message && <p className={styles.message}>{message}</p>}
        {error && <p className={styles.error}>{error}</p>}
        <label className={styles.label}>
          이메일
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className={styles.input}
            placeholder="example@email.com"
          />
        </label>
        <label className={styles.label}>
          비밀번호
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={styles.input}
            placeholder="비밀번호 입력"
          />
        </label>
        <button type="submit" className={styles.submitBtn} disabled={loading}>
          {loading ? '처리 중...' : '로그인'}
        </button>
      </form>
      <p className={styles.footer}>
        계정이 없으신가요? <Link to="/signup" className={styles.link}>회원가입</Link>
      </p>
    </div>
  );
}

function isAxiosError(err: unknown): err is { response?: { data?: { message?: string } } } {
  return typeof err === 'object' && err !== null && 'response' in err;
}

export default LoginPage;
