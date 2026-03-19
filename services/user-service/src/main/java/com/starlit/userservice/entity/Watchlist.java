package com.starlit.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 관심종목 엔티티.
 *
 * <p>watchlist 테이블과 매핑된다. (user_id, stock_code) 조합은 유니크 제약을 갖는다.</p>
 *
 * <pre>
 * CREATE TABLE watchlist (
 *     id         BIGSERIAL PRIMARY KEY,
 *     user_id    BIGINT NOT NULL,
 *     stock_code VARCHAR(20) NOT NULL,
 *     stock_name VARCHAR(100) NOT NULL,
 *     created_at TIMESTAMP DEFAULT NOW(),
 *     UNIQUE(user_id, stock_code)
 * );
 * </pre>
 */
@Entity
@Table(name = "watchlist", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "stock_code"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소유 사용자 ID (users.id 참조) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 종목 코드 (예: "005930") */
    @Column(name = "stock_code", nullable = false, length = 20)
    private String stockCode;

    /** 종목명 (예: "삼성전자") */
    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    /** 등록 일시 (자동 설정) */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
