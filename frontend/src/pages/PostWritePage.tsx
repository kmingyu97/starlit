import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import client from '../api/client';
import styles from './PostWritePage.module.css';

const CATEGORIES = ['자유', '종목토론', '뉴스', '질문'];

/** 게시글 작성 페이지. */
function PostWritePage() {
  const navigate = useNavigate();
  const { isLoggedIn, nickname } = useAuth();
  const [category, setCategory] = useState('자유');
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isLoggedIn) {
    navigate('/login');
    return null;
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');

    if (!title.trim() || !content.trim()) {
      setError('제목과 내용을 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      const { data } = await client.post(`/community/posts?nickname=${encodeURIComponent(nickname ?? '익명')}`, {
        category,
        title: title.trim(),
        content: content.trim(),
      });
      navigate(`/community/${data.id}`);
    } catch (err: unknown) {
      if (isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('글 작성에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>글쓰기</h2>
      <form onSubmit={handleSubmit} className={styles.form}>
        {error && <p className={styles.error}>{error}</p>}
        <select value={category} onChange={(e) => setCategory(e.target.value)} className={styles.select}>
          {CATEGORIES.map((cat) => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="제목"
          className={styles.input}
          maxLength={200}
        />
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="내용을 입력하세요"
          className={styles.textarea}
          rows={12}
        />
        <button type="submit" className={styles.submitBtn} disabled={loading}>
          {loading ? '작성 중...' : '등록'}
        </button>
      </form>
    </div>
  );
}

function isAxiosError(err: unknown): err is { response?: { data?: { message?: string } } } {
  return typeof err === 'object' && err !== null && 'response' in err;
}

export default PostWritePage;
