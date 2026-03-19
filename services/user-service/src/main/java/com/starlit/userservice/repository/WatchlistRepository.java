package com.starlit.userservice.repository;

import com.starlit.userservice.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserId(Long userId);

    boolean existsByUserIdAndStockCode(Long userId, String stockCode);

    void deleteByUserIdAndStockCode(Long userId, String stockCode);
}
