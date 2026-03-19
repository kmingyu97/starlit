import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Layout from './components/Layout';
import HomePage from './pages/HomePage';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import StockListPage from './pages/StockListPage';
import StockDetailPage from './pages/StockDetailPage';
import HeatmapPage from './pages/HeatmapPage';
import CommunityPage from './pages/CommunityPage';
import PostWritePage from './pages/PostWritePage';
import PostDetailPage from './pages/PostDetailPage';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/stocks" element={<StockListPage />} />
            <Route path="/stocks/:stockCode" element={<StockDetailPage />} />
            <Route path="/heatmap" element={<HeatmapPage />} />
            <Route path="/community" element={<CommunityPage />} />
            <Route path="/community/write" element={<PostWritePage />} />
            <Route path="/community/:id" element={<PostDetailPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
