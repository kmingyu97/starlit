package com.starlit.userservice.repository;

import com.starlit.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 엔티티 JPA Repository.
 *
 * <p>회원가입 시 이메일/닉네임 중복 체크, 로그인 시 이메일 조회에 사용된다.</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 이메일로 사용자 조회 (로그인 시 사용) */
    Optional<User> findByEmail(String email);

    /** 이메일 중복 여부 확인 (회원가입 시 사용) */
    boolean existsByEmail(String email);

    /** 닉네임 중복 여부 확인 (회원가입, 프로필 수정 시 사용) */
    boolean existsByNickname(String nickname);
}
