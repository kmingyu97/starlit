package com.starlit.stockservice.repository;

import com.starlit.stockservice.entity.StockMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 종목 마스터 Repository.
 *
 * <p>종목 검색(이름/코드), 시장별 필터, 섹터별 조회 등을 지원한다.</p>
 */
public interface StockMasterRepository extends JpaRepository<StockMaster, String> {

    /** 종목명 또는 종목코드로 검색한다 (LIKE). */
    @Query("SELECT s FROM StockMaster s WHERE s.stockName LIKE %:keyword% OR s.stockCode LIKE %:keyword%")
    Page<StockMaster> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 시장별 종목 목록을 조회한다. */
    Page<StockMaster> findByMarket(String market, Pageable pageable);

    /** 섹터별 종목 목록을 조회한다. */
    List<StockMaster> findBySector(String sector);

    /** 전체 섹터 목록을 조회한다 (중복 제거). */
    @Query("SELECT DISTINCT s.sector FROM StockMaster s WHERE s.sector IS NOT NULL ORDER BY s.sector")
    List<String> findDistinctSectors();
}
