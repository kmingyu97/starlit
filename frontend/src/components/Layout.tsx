import { Outlet } from 'react-router-dom';
import Header from './Header';
import styles from './Layout.module.css';

/** 공통 레이아웃. Header + 콘텐츠 영역으로 구성된다. */
function Layout() {
  return (
    <div className={styles.layout}>
      <Header />
      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
