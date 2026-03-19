import { createContext, useContext, useState, useCallback, type ReactNode } from 'react';

interface AuthState {
  isLoggedIn: boolean;
  nickname: string | null;
  login: (token: string, nickname: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

/** 인증 상태(토큰·닉네임)를 관리하는 Provider. */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem('token'));
  const [nickname, setNickname] = useState(() => localStorage.getItem('nickname'));

  const login = useCallback((token: string, nick: string) => {
    localStorage.setItem('token', token);
    localStorage.setItem('nickname', nick);
    setIsLoggedIn(true);
    setNickname(nick);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('nickname');
    setIsLoggedIn(false);
    setNickname(null);
  }, []);

  return (
    <AuthContext.Provider value={{ isLoggedIn, nickname, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

/** AuthContext를 사용하는 커스텀 훅. Provider 바깥에서 호출하면 에러를 던진다. */
export function useAuth(): AuthState {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
