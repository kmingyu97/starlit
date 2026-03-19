import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import client from '../api/client';
import styles from './StockDetailPage.module.css';

interface StockDetail {
  stockCode: string;
  stockName: string;
  market: string;
  sector: string;
  marketCap: number | null;
  tradeDate: string | null;
  openPrice: number | null;
  highPrice: number | null;
  lowPrice: number | null;
  closePrice: number | null;
  volume: number | null;
  changeRate: number | null;
}

/** 종목 상세 페이지. 종목 기본 정보와 최근 시세를 표시한다. */
function StockDetailPage() {
  const { stockCode } = useParams<{ stockCode: string }>();
  const [stock, setStock] = useState<StockDetail | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!stockCode) return;
    client
      .get<StockDetail>(`/stocks/${stockCode}`)
      .then(({ data }) => setStock(data))
      .catch(() => setError('종목 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, [stockCode]);

  const formatMarketCap = (cap: number | null) => {
    if (!cap) return '-';
    if (cap >= 1_0000_0000_0000) return `${(cap / 1_0000_0000_0000).toFixed(1)}조원`;
    if (cap >= 1_0000_0000) return `${(cap / 1_0000_0000).toFixed(0)}억원`;
    return `${cap.toLocaleString()}원`;
  };

  if (loading) return <p className={styles.loading}>로딩 중...</p>;
  if (error || !stock) return <p className={styles.error}>{error || '종목을 찾을 수 없습니다.'}</p>;

  return (
    <div className={styles.container}>
      <Link to="/stocks" className={styles.back}>종목 목록으로</Link>

      <div className={styles.header}>
        <h2 className={styles.name}>{stock.stockName}</h2>
        <span className={styles.code}>{stock.stockCode}</span>
        <span className={styles.market}>{stock.market}</span>
        {stock.sector && <span className={styles.sector}>{stock.sector}</span>}
      </div>

      <div className={styles.priceSection}>
        <span className={styles.price}>
          {stock.closePrice?.toLocaleString() ?? '-'}
          <small>원</small>
        </span>
        {stock.changeRate != null && (
          <span className={stock.changeRate >= 0 ? styles.up : styles.down}>
            {stock.changeRate >= 0 ? '+' : ''}{stock.changeRate.toFixed(2)}%
          </span>
        )}
      </div>

      <div className={styles.infoGrid}>
        <div className={styles.infoItem}>
          <span className={styles.label}>시가</span>
          <span className={styles.value}>{stock.openPrice?.toLocaleString() ?? '-'}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>고가</span>
          <span className={styles.value}>{stock.highPrice?.toLocaleString() ?? '-'}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>저가</span>
          <span className={styles.value}>{stock.lowPrice?.toLocaleString() ?? '-'}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>거래량</span>
          <span className={styles.value}>{stock.volume?.toLocaleString() ?? '-'}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>시가총액</span>
          <span className={styles.value}>{formatMarketCap(stock.marketCap)}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>거래일</span>
          <span className={styles.value}>{stock.tradeDate ?? '-'}</span>
        </div>
      </div>
    </div>
  );
}

export default StockDetailPage;
