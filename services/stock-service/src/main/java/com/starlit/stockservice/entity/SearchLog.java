package com.starlit.stockservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 검색 로그 엔티티.
 *
 * <p>종목 검색 이력을 저장하여 인기 검색 종목 집계에 활용한다.</p>
 */
@Entity
@Table(name = "search_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 검색된 종목 코드 */
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    /** 검색 시각 */
    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    public SearchLog(String stockCode) {
        this.stockCode = stockCode;
        this.searchedAt = LocalDateTime.now();
    }
}
