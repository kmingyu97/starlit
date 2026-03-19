import { useEffect, useState, type FormEvent } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import client from '../api/client';
import styles from './PostDetailPage.module.css';

interface PostDetail {
  id: number;
  userId: number;
  nickname: string;
  category: string;
  title: string;
  content: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string;
}

interface CommentItem {
  id: number;
  userId: number;
  nickname: string;
  content: string;
  createdAt: string;
}

/** 게시글 상세 페이지. 본문, 댓글, 좋아요를 표시한다. */
function PostDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isLoggedIn, nickname } = useAuth();
  const [post, setPost] = useState<PostDetail | null>(null);
  const [comments, setComments] = useState<CommentItem[]>([]);
  const [commentText, setCommentText] = useState('');
  const [liked, setLiked] = useState(false);
  const [loading, setLoading] = useState(true);

  const userId = isLoggedIn ? getUserIdFromToken() : null;

  useEffect(() => {
    if (!id) return;
    Promise.all([
      client.get<PostDetail>(`/community/posts/${id}`),
      client.get<CommentItem[]>(`/community/posts/${id}/comments`),
    ])
      .then(([postRes, commentsRes]) => {
        setPost(postRes.data);
        setComments(commentsRes.data);
      })
      .catch(() => navigate('/community'))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleLike = async () => {
    if (!isLoggedIn) return navigate('/login');
    try {
      const { data } = await client.post<{ liked: boolean; likeCount: number }>(
        `/community/posts/${id}/like`,
      );
      setLiked(data.liked);
      setPost((prev) => prev ? { ...prev, likeCount: data.likeCount } : prev);
    } catch { /* ignore */ }
  };

  const handleComment = async (e: FormEvent) => {
    e.preventDefault();
    if (!commentText.trim()) return;
    try {
      const { data } = await client.post<CommentItem>(
        `/community/posts/${id}/comments?nickname=${encodeURIComponent(nickname ?? '익명')}`,
        { content: commentText.trim() },
      );
      setComments((prev) => [...prev, data]);
      setCommentText('');
      setPost((prev) => prev ? { ...prev, commentCount: prev.commentCount + 1 } : prev);
    } catch { /* ignore */ }
  };

  const handleDeleteComment = async (commentId: number) => {
    try {
      await client.delete(`/community/comments/${commentId}`);
      setComments((prev) => prev.filter((c) => c.id !== commentId));
      setPost((prev) => prev ? { ...prev, commentCount: prev.commentCount - 1 } : prev);
    } catch { /* ignore */ }
  };

  const handleDeletePost = async () => {
    try {
      await client.delete(`/community/posts/${id}`);
      navigate('/community');
    } catch { /* ignore */ }
  };

  if (loading) return <p className={styles.loading}>로딩 중...</p>;
  if (!post) return null;

  const isAuthor = userId !== null && post.userId === userId;

  return (
    <div className={styles.container}>
      <Link to="/community" className={styles.back}>목록으로</Link>

      <article className={styles.article}>
        <span className={styles.cat}>{post.category}</span>
        <h2 className={styles.title}>{post.title}</h2>
        <div className={styles.meta}>
          {post.nickname} · {new Date(post.createdAt).toLocaleDateString('ko-KR')} · 조회 {post.viewCount}
        </div>
        {isAuthor && (
          <div className={styles.actions}>
            <button onClick={handleDeletePost} className={styles.deleteBtn}>삭제</button>
          </div>
        )}
        <div className={styles.content}>{post.content}</div>
        <button onClick={handleLike} className={`${styles.likeBtn} ${liked ? styles.liked : ''}`}>
          {liked ? '좋아요 취소' : '좋아요'} {post.likeCount}
        </button>
      </article>

      <section className={styles.commentSection}>
        <h3 className={styles.commentTitle}>댓글 {post.commentCount}</h3>
        <div className={styles.commentList}>
          {comments.map((c) => (
            <div key={c.id} className={styles.comment}>
              <div className={styles.commentHeader}>
                <span className={styles.commentNick}>{c.nickname}</span>
                <span className={styles.commentDate}>
                  {new Date(c.createdAt).toLocaleDateString('ko-KR')}
                </span>
                {userId !== null && c.userId === userId && (
                  <button onClick={() => handleDeleteComment(c.id)} className={styles.commentDeleteBtn}>
                    삭제
                  </button>
                )}
              </div>
              <p className={styles.commentContent}>{c.content}</p>
            </div>
          ))}
        </div>
        {isLoggedIn && (
          <form onSubmit={handleComment} className={styles.commentForm}>
            <input
              type="text"
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              placeholder="댓글을 입력하세요"
              className={styles.commentInput}
            />
            <button type="submit" className={styles.commentBtn}>등록</button>
          </form>
        )}
      </section>
    </div>
  );
}

/** localStorage의 토큰에서 userId를 추출한다 (JWT payload 디코딩). */
function getUserIdFromToken(): number | null {
  const token = localStorage.getItem('token');
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return Number(payload.sub);
  } catch {
    return null;
  }
}

export default PostDetailPage;
