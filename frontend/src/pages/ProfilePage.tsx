import { useEffect, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import client from '../api/client';
import styles from './ProfilePage.module.css';

interface Profile {
  id: number;
  email: string;
  nickname: string;
  createdAt: string;
}

/** 프로필 페이지. 내 정보를 조회하고 닉네임을 수정할 수 있다. */
function ProfilePage() {
  const navigate = useNavigate();
  const { isLoggedIn, login } = useAuth();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [nickname, setNickname] = useState('');
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }

    client
      .get<Profile>('/users/me')
      .then(({ data }) => {
        setProfile(data);
        setNickname(data.nickname);
      })
      .catch(() => setError('프로필을 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, [isLoggedIn, navigate]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (nickname.length < 2 || nickname.length > 30) {
      setError('닉네임은 2~30자여야 합니다.');
      return;
    }

    try {
      const { data } = await client.put<Profile>('/users/me', { nickname });
      setProfile(data);
      setEditing(false);
      setSuccess('닉네임이 변경되었습니다.');
      login(localStorage.getItem('token')!, data.nickname);
    } catch (err: unknown) {
      if (isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('닉네임 변경에 실패했습니다.');
      }
    }
  };

  const handleCancel = () => {
    setEditing(false);
    setNickname(profile?.nickname ?? '');
    setError('');
    setSuccess('');
  };

  if (loading) {
    return <p className={styles.loading}>로딩 중...</p>;
  }

  if (!profile) {
    return <p className={styles.error}>{error || '프로필을 불러올 수 없습니다.'}</p>;
  }

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>내 정보</h2>
      {error && <p className={styles.errorMsg}>{error}</p>}
      {success && <p className={styles.successMsg}>{success}</p>}
      <div className={styles.card}>
        <div className={styles.row}>
          <span className={styles.label}>이메일</span>
          <span className={styles.value}>{profile.email}</span>
        </div>
        <div className={styles.row}>
          <span className={styles.label}>닉네임</span>
          {editing ? (
            <form onSubmit={handleSubmit} className={styles.editForm}>
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                className={styles.input}
              />
              <button type="submit" className={styles.saveBtn}>저장</button>
              <button type="button" onClick={handleCancel} className={styles.cancelBtn}>취소</button>
            </form>
          ) : (
            <span className={styles.value}>
              {profile.nickname}
              <button onClick={() => setEditing(true)} className={styles.editBtn}>수정</button>
            </span>
          )}
        </div>
        <div className={styles.row}>
          <span className={styles.label}>가입일</span>
          <span className={styles.value}>{new Date(profile.createdAt).toLocaleDateString('ko-KR')}</span>
        </div>
      </div>
    </div>
  );
}

function isAxiosError(err: unknown): err is { response?: { data?: { message?: string } } } {
  return typeof err === 'object' && err !== null && 'response' in err;
}

export default ProfilePage;
