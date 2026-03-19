import styles from './HomePage.module.css';

/** 메인 페이지. 추후 대시보드(지수·환율·히트맵)가 들어갈 자리. */
function HomePage() {
  return (
    <div className={styles.container}>
      <h1 className={styles.title}>STARLIT</h1>
      <p className={styles.subtitle}>주식시장을 한눈에</p>
    </div>
  );
}

export default HomePage;
