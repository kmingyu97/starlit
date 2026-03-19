import { useEffect, useState } from 'react';
import client from '../api/client';
import styles from './HomePage.module.css';

interface Index {
  name: string;
  value: number;
  change: number;
  changeRate: number;
}

interface ExchangeRate {
  currency: string;
  rate: number;
  change: number;
  changeRate: number;
}

interface PopularStock {
  rank: number;
  stockCode: string;
  stockName: string;
  searchCount: number;
}

/** 메인 대시보드. 주요 지수, 환율, 인기 검색 종목을 표시한다. */
function HomePage() {
  const [indices, setIndices] = useState<Index[]>([]);
  const [rates, setRates] = useState<ExchangeRate[]>([]);
  const [popular, setPopular] = useState<PopularStock[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      client.get<Index[]>('/stocks/indices'),
      client.get<ExchangeRate[]>('/stocks/exchange-rates'),
      client.get<PopularStock[]>('/stocks/popular'),
    ])
      .then(([indicesRes, ratesRes, popularRes]) => {
        setIndices(indicesRes.data);
        setRates(ratesRes.data);
        setPopular(popularRes.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <p className={styles.loading}>로딩 중...</p>;
  }

  return (
    <div className={styles.dashboard}>
      <h1 className={styles.title}>STARLIT</h1>
      <p className={styles.subtitle}>주식시장을 한눈에</p>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>주요 지수</h2>
        <div className={styles.cardGrid}>
          {indices.map((idx) => (
            <div key={idx.name} className={styles.card}>
              <span className={styles.cardLabel}>{idx.name}</span>
              <span className={styles.cardValue}>{idx.value.toLocaleString()}</span>
              <span className={idx.change >= 0 ? styles.up : styles.down}>
                {idx.change >= 0 ? '+' : ''}{idx.change.toFixed(2)} ({idx.changeRate >= 0 ? '+' : ''}{idx.changeRate.toFixed(2)}%)
              </span>
            </div>
          ))}
        </div>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>환율</h2>
        <div className={styles.cardGrid}>
          {rates.map((rate) => (
            <div key={rate.currency} className={styles.card}>
              <span className={styles.cardLabel}>{rate.currency}/KRW</span>
              <span className={styles.cardValue}>{rate.rate.toLocaleString()}</span>
              <span className={rate.change >= 0 ? styles.up : styles.down}>
                {rate.change >= 0 ? '+' : ''}{rate.change.toFixed(2)} ({rate.changeRate >= 0 ? '+' : ''}{rate.changeRate.toFixed(2)}%)
              </span>
            </div>
          ))}
        </div>
      </section>

      {popular.length > 0 && (
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>인기 검색 종목</h2>
          <div className={styles.popularList}>
            {popular.map((stock) => (
              <div key={stock.stockCode} className={styles.popularItem}>
                <span className={styles.rank}>{stock.rank}</span>
                <span className={styles.stockName}>{stock.stockName}</span>
                <span className={styles.stockCode}>{stock.stockCode}</span>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}

export default HomePage;
