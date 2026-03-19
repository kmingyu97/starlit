import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import client from '../api/client';
import styles from './CommunityPage.module.css';

interface PostItem {
  id: number;
  nickname: string;
  category: string;
  title: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string;
}

interface PageResponse {
  content: PostItem[];
  totalPages: number;
  number: number;
}

const CATEGORIES = ['', '자유', '종목토론', '뉴스', '질문'];

/** 커뮤니티 게시판 목록 페이지. */
function CommunityPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const { isLoggedIn } = useAuth();
  const [posts, setPosts] = useState<PostItem[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const category = searchParams.get('category') ?? '';
  const page = Number(searchParams.get('page') ?? '0');

  useEffect(() => {
    setLoading(true);
    const params: Record<string, string> = { page: String(page), size: '20' };
    if (category) params.category = category;

    client
      .get<PageResponse>('/community/posts', { params })
      .then(({ data }) => {
        setPosts(data.content);
        setTotalPages(data.totalPages);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [category, page]);

  const handleCategory = (cat: string) => {
    const params: Record<string, string> = {};
    if (cat) params.category = cat;
    setSearchParams(params);
  };

  const goToPage = (p: number) => {
    const params = new URLSearchParams(searchParams);
    params.set('page', String(p));
    setSearchParams(params);
  };

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return `${date.getMonth() + 1}/${date.getDate()}`;
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2 className={styles.title}>커뮤니티</h2>
        {isLoggedIn && (
          <Link to="/community/write" className={styles.writeBtn}>글쓰기</Link>
        )}
      </div>

      <div className={styles.categories}>
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => handleCategory(cat)}
            className={`${styles.catBtn} ${category === cat ? styles.active : ''}`}
          >
            {cat || '전체'}
          </button>
        ))}
      </div>

      {loading ? (
        <p className={styles.loading}>로딩 중...</p>
      ) : posts.length === 0 ? (
        <p className={styles.empty}>게시글이 없습니다.</p>
      ) : (
        <>
          <div className={styles.postList}>
            {posts.map((post) => (
              <Link key={post.id} to={`/community/${post.id}`} className={styles.postItem}>
                <span className={styles.cat}>{post.category}</span>
                <span className={styles.postTitle}>
                  {post.title}
                  {post.commentCount > 0 && (
                    <span className={styles.commentBadge}>[{post.commentCount}]</span>
                  )}
                </span>
                <span className={styles.meta}>
                  {post.nickname} · {formatDate(post.createdAt)} · 조회 {post.viewCount} · 좋아요 {post.likeCount}
                </span>
              </Link>
            ))}
          </div>

          {totalPages > 1 && (
            <div className={styles.pagination}>
              <button onClick={() => goToPage(page - 1)} disabled={page === 0} className={styles.pageBtn}>이전</button>
              <span className={styles.pageInfo}>{page + 1} / {totalPages}</span>
              <button onClick={() => goToPage(page + 1)} disabled={page >= totalPages - 1} className={styles.pageBtn}>다음</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default CommunityPage;
