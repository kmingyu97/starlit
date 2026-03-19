package com.starlit.stockservice.repository;

import com.starlit.stockservice.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 검색 로그 Repository.
 *
 * <p>인기 검색 종목 집계를 위한 쿼리를 제공한다.</p>
 */
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    /**
     * 인기 검색 종목 TOP N을 조회한다.
     *
     * <p>검색 횟수 기준 내림차순으로 종목 코드와 검색 횟수를 반환한다.</p>
     */
    @Query("SELECT s.stockCode, COUNT(s) AS cnt FROM SearchLog s GROUP BY s.stockCode ORDER BY cnt DESC")
    List<Object[]> findPopularStockCodes();
}
