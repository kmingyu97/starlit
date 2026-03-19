import { useEffect, useState, type FormEvent } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import client from '../api/client';
import styles from './StockListPage.module.css';

interface Stock {
  stockCode: string;
  stockName: string;
  market: string;
  sector: string;
  closePrice: number | null;
  changeRate: number | null;
  marketCap: number | null;
}

interface PageResponse {
  content: Stock[];
  totalPages: number;
  totalElements: number;
  number: number;
}

/** 종목 목록 페이지. 검색·시장 필터·페이징을 지원한다. */
function StockListPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [keyword, setKeyword] = useState(searchParams.get('keyword') ?? '');
  const [market, setMarket] = useState(searchParams.get('market') ?? '');
  const page = Number(searchParams.get('page') ?? '0');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    const params: Record<string, string> = { page: String(page), size: '20' };
    const kw = searchParams.get('keyword');
    const mk = searchParams.get('market');
    if (kw) params.keyword = kw;
    if (mk) params.market = mk;

    client
      .get<PageResponse>('/stocks', { params })
      .then(({ data }) => {
        setStocks(data.content);
        setTotalPages(data.totalPages);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [searchParams, page]);

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    const params: Record<string, string> = {};
    if (keyword.trim()) params.keyword = keyword.trim();
    if (market) params.market = market;
    setSearchParams(params);
  };

  const handleMarketFilter = (m: string) => {
    setMarket(m);
    const params: Record<string, string> = {};
    if (keyword.trim()) params.keyword = keyword.trim();
    if (m) params.market = m;
    setSearchParams(params);
  };

  const goToPage = (p: number) => {
    const params = new URLSearchParams(searchParams);
    params.set('page', String(p));
    setSearchParams(params);
  };

  const formatMarketCap = (cap: number | null) => {
    if (!cap) return '-';
    if (cap >= 1_0000_0000_0000) return `${(cap / 1_0000_0000_0000).toFixed(1)}조`;
    if (cap >= 1_0000_0000) return `${(cap / 1_0000_0000).toFixed(0)}억`;
    return cap.toLocaleString();
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>종목 탐색</h2>

      <form onSubmit={handleSearch} className={styles.searchBar}>
        <input
          type="text"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="종목명 또는 코드 검색"
          className={styles.searchInput}
        />
        <button type="submit" className={styles.searchBtn}>검색</button>
      </form>

      <div className={styles.filters}>
        {['', 'KOSPI', 'KOSDAQ'].map((m) => (
          <button
            key={m}
            onClick={() => handleMarketFilter(m)}
            className={`${styles.filterBtn} ${market === m ? styles.active : ''}`}
          >
            {m || '전체'}
          </button>
        ))}
      </div>

      {loading ? (
        <p className={styles.loading}>로딩 중...</p>
      ) : stocks.length === 0 ? (
        <p className={styles.empty}>검색 결과가 없습니다.</p>
      ) : (
        <>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>종목명</th>
                <th>코드</th>
                <th>시장</th>
                <th className={styles.right}>종가</th>
                <th className={styles.right}>등락률</th>
                <th className={styles.right}>시가총액</th>
              </tr>
            </thead>
            <tbody>
              {stocks.map((stock) => (
                <tr key={stock.stockCode}>
                  <td>
                    <Link to={`/stocks/${stock.stockCode}`} className={styles.stockLink}>
                      {stock.stockName}
                    </Link>
                  </td>
                  <td className={styles.code}>{stock.stockCode}</td>
                  <td>{stock.market}</td>
                  <td className={styles.right}>
                    {stock.closePrice?.toLocaleString() ?? '-'}
                  </td>
                  <td className={`${styles.right} ${stock.changeRate != null ? (stock.changeRate >= 0 ? styles.up : styles.down) : ''}`}>
                    {stock.changeRate != null
                      ? `${stock.changeRate >= 0 ? '+' : ''}${stock.changeRate.toFixed(2)}%`
                      : '-'}
                  </td>
                  <td className={styles.right}>{formatMarketCap(stock.marketCap)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {totalPages > 1 && (
            <div className={styles.pagination}>
              <button onClick={() => goToPage(page - 1)} disabled={page === 0} className={styles.pageBtn}>
                이전
              </button>
              <span className={styles.pageInfo}>{page + 1} / {totalPages}</span>
              <button onClick={() => goToPage(page + 1)} disabled={page >= totalPages - 1} className={styles.pageBtn}>
                다음
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default StockListPage;
