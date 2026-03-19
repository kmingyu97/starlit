package com.starlit.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * user-service 애플리케이션 진입점.
 *
 * <p>회원가입, 로그인(JWT 발급), 프로필 관리, 관심종목 CRUD를 담당한다.
 * Gateway에서 JWT를 검증한 뒤 {@code X-User-Id} 헤더로 유저 정보를 전달받는다.</p>
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
