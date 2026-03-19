import { useEffect, useState } from 'react';
import client from '../api/client';
import styles from './HeatmapPage.module.css';

interface HeatmapStock {
  stockCode: string;
  stockName: string;
  changeRate: number | null;
  marketCap: number | null;
}

interface HeatmapSector {
  sector: string;
  stocks: HeatmapStock[];
}

/** 히트맵 페이지. 섹터별 종목 등락률을 시각적으로 표시한다. */
function HeatmapPage() {
  const [sectors, setSectors] = useState<HeatmapSector[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    client
      .get<HeatmapSector[]>('/stocks/heatmap')
      .then(({ data }) => setSectors(data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const getColor = (rate: number | null): string => {
    if (rate == null) return '#374151';
    if (rate >= 3) return '#dc2626';
    if (rate >= 2) return '#ef4444';
    if (rate >= 1) return '#f87171';
    if (rate > 0) return '#fca5a5';
    if (rate === 0) return '#6b7280';
    if (rate > -1) return '#93c5fd';
    if (rate > -2) return '#60a5fa';
    if (rate > -3) return '#3b82f6';
    return '#2563eb';
  };

  if (loading) return <p className={styles.loading}>로딩 중...</p>;

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>섹터별 히트맵</h2>

      <div className={styles.legend}>
        <span className={styles.legendDown}>하락</span>
        <div className={styles.legendBar} />
        <span className={styles.legendUp}>상승</span>
      </div>

      <div className={styles.sectors}>
        {sectors.map((sector) => (
          <div key={sector.sector} className={styles.sector}>
            <h3 className={styles.sectorName}>{sector.sector}</h3>
            <div className={styles.stockGrid}>
              {sector.stocks.map((stock) => (
                <div
                  key={stock.stockCode}
                  className={styles.stockCell}
                  style={{ backgroundColor: getColor(stock.changeRate) }}
                  title={`${stock.stockName} (${stock.stockCode})`}
                >
                  <span className={styles.cellName}>{stock.stockName}</span>
                  <span className={styles.cellRate}>
                    {stock.changeRate != null
                      ? `${stock.changeRate >= 0 ? '+' : ''}${stock.changeRate.toFixed(2)}%`
                      : '-'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default HeatmapPage;
