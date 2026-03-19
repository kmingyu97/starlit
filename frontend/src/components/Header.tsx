import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import styles from './Header.module.css';

/** 상단 네비게이션 바. 로그인 상태에 따라 메뉴가 달라진다. */
function Header() {
  const { isLoggedIn, nickname, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className={styles.header}>
      <Link to="/" className={styles.logo}>
        STARLIT
      </Link>
      <nav className={styles.nav}>
        <Link to="/stocks" className={styles.link}>
          종목
        </Link>
        <Link to="/heatmap" className={styles.link}>
          히트맵
        </Link>
        {isLoggedIn ? (
          <>
            <span className={styles.nickname}>{nickname}</span>
            <Link to="/profile" className={styles.link}>
              내 정보
            </Link>
            <button onClick={handleLogout} className={styles.logoutBtn}>
              로그아웃
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className={styles.link}>
              로그인
            </Link>
            <Link to="/signup" className={styles.link}>
              회원가입
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}

export default Header;
