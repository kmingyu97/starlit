package com.starlit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gateway 애플리케이션 진입점.
 *
 * <p>모든 클라이언트 요청의 단일 진입점으로, JWT 검증·라우팅·CORS 처리를 담당한다.
 * 인증이 필요한 요청은 JWT를 검증한 뒤 {@code X-User-Id}, {@code X-User-Email} 헤더를
 * 하위 서비스에 주입한다.</p>
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
