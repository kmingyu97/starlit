package com.starlit.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 회원 엔티티.
 *
 * <p>users 테이블과 매핑된다. email과 nickname은 각각 유니크 제약을 갖는다.</p>
 *
 * <pre>
 * CREATE TABLE users (
 *     id         BIGSERIAL PRIMARY KEY,
 *     email      VARCHAR(100) UNIQUE NOT NULL,
 *     password   VARCHAR(255) NOT NULL,       -- BCrypt 해시값
 *     nickname   VARCHAR(30) UNIQUE NOT NULL,
 *     created_at TIMESTAMP DEFAULT NOW()
 * );
 * </pre>
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인용 이메일 (유니크) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** BCrypt로 해싱된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /** 표시용 닉네임 (유니크) */
    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    /** 가입 일시 (자동 설정) */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 닉네임을 변경한다.
     *
     * @param nickname 새 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
