package com.starlit.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정.
 *
 * <p>Gateway에서 일괄 처리하여 하위 서비스에서는 별도 CORS 설정이 불필요하다.</p>
 *
 * <ul>
 *   <li>허용 Origin: {@code http://localhost:5173} (프론트엔드)</li>
 *   <li>허용 메서드: GET, POST, PUT, DELETE, OPTIONS</li>
 *   <li>허용 헤더: Authorization, Content-Type</li>
 *   <li>Credentials: true (인증 헤더 허용)</li>
 * </ul>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true);
    }
}
