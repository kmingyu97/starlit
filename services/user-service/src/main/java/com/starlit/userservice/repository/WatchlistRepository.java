package com.starlit.userservice.repository;

import com.starlit.userservice.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Watchlist 엔티티 JPA Repository.
 *
 * <p>사용자별 관심종목 조회, 중복 확인, 삭제에 사용된다.</p>
 */
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    /** 특정 사용자의 관심종목 목록 조회 */
    List<Watchlist> findByUserId(Long userId);

    /** 특정 사용자가 해당 종목을 이미 등록했는지 확인 (중복 방지) */
    boolean existsByUserIdAndStockCode(Long userId, String stockCode);

    /** 특정 사용자의 관심종목 삭제 */
    void deleteByUserIdAndStockCode(Long userId, String stockCode);
}
