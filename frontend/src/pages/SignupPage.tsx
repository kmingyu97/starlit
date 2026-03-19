import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import client from '../api/client';
import styles from './AuthForm.module.css';

/** 회원가입 페이지. 이메일·비밀번호·닉네임을 입력받아 가입 요청을 보낸다. */
function SignupPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (!email || !password || !nickname) {
      setError('모든 항목을 입력해주세요.');
      return;
    }
    if (password.length < 8) {
      setError('비밀번호는 8자 이상이어야 합니다.');
      return;
    }
    if (nickname.length < 2 || nickname.length > 30) {
      setError('닉네임은 2~30자여야 합니다.');
      return;
    }

    setLoading(true);
    try {
      await client.post('/users/signup', { email, password, nickname });
      navigate('/login', { state: { message: '회원가입이 완료되었습니다. 로그인해주세요.' } });
    } catch (err: unknown) {
      if (isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('회원가입에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>회원가입</h2>
      <form onSubmit={handleSubmit} className={styles.form}>
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
            placeholder="8자 이상"
          />
        </label>
        <label className={styles.label}>
          닉네임
          <input
            type="text"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            className={styles.input}
            placeholder="2~30자"
          />
        </label>
        <button type="submit" className={styles.submitBtn} disabled={loading}>
          {loading ? '처리 중...' : '회원가입'}
        </button>
      </form>
      <p className={styles.footer}>
        이미 계정이 있으신가요? <Link to="/login" className={styles.link}>로그인</Link>
      </p>
    </div>
  );
}

function isAxiosError(err: unknown): err is { response?: { data?: { message?: string } } } {
  return typeof err === 'object' && err !== null && 'response' in err;
}

export default SignupPage;
