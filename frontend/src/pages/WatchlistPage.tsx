import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import client from '../api/client';
import styles from './WatchlistPage.module.css';

interface WatchlistItem {
  id: number;
  stockCode: string;
  stockName: string;
  createdAt: string;
}

/** 관심종목 페이지. 등록된 관심종목 목록을 관리한다. */
function WatchlistPage() {
  const navigate = useNavigate();
  const { isLoggedIn } = useAuth();
  const [watchlist, setWatchlist] = useState<WatchlistItem[]>([]);
  const [stockCode, setStockCode] = useState('');
  const [stockName, setStockName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }
    fetchWatchlist();
  }, [isLoggedIn, navigate]);

  const fetchWatchlist = () => {
    client
      .get<WatchlistItem[]>('/users/watchlist')
      .then(({ data }) => setWatchlist(data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  const handleAdd = async () => {
    setError('');
    if (!stockCode.trim() || !stockName.trim()) {
      setError('종목 코드와 이름을 입력해주세요.');
      return;
    }
    try {
      await client.post('/users/watchlist', {
        stockCode: stockCode.trim(),
        stockName: stockName.trim(),
      });
      setStockCode('');
      setStockName('');
      fetchWatchlist();
    } catch (err: unknown) {
      if (isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('관심종목 추가에 실패했습니다.');
      }
    }
  };

  const handleDelete = async (code: string) => {
    try {
      await client.delete(`/users/watchlist/${code}`);
      setWatchlist((prev) => prev.filter((w) => w.stockCode !== code));
    } catch { /* ignore */ }
  };

  if (loading) return <p className={styles.loading}>로딩 중...</p>;

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>관심종목</h2>

      <div className={styles.addForm}>
        <input
          type="text"
          value={stockCode}
          onChange={(e) => setStockCode(e.target.value)}
          placeholder="종목 코드 (예: 005930)"
          className={styles.input}
        />
        <input
          type="text"
          value={stockName}
          onChange={(e) => setStockName(e.target.value)}
          placeholder="종목명 (예: 삼성전자)"
          className={styles.input}
        />
        <button onClick={handleAdd} className={styles.addBtn}>추가</button>
      </div>
      {error && <p className={styles.error}>{error}</p>}

      {watchlist.length === 0 ? (
        <p className={styles.empty}>등록된 관심종목이 없습니다.</p>
      ) : (
        <div className={styles.list}>
          {watchlist.map((item) => (
            <div key={item.id} className={styles.item}>
              <Link to={`/stocks/${item.stockCode}`} className={styles.stockInfo}>
                <span className={styles.name}>{item.stockName}</span>
                <span className={styles.code}>{item.stockCode}</span>
              </Link>
              <button onClick={() => handleDelete(item.stockCode)} className={styles.deleteBtn}>
                삭제
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function isAxiosError(err: unknown): err is { response?: { data?: { message?: string } } } {
  return typeof err === 'object' && err !== null && 'response' in err;
}

export default WatchlistPage;
