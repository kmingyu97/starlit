package com.starlit.gateway.filter;

import com.starlit.gateway.config.JwtProvider;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.Map;

/**
 * JWT 인증 필터.
 *
 * <p>모든 라우트에 적용되며, 다음 순서로 동작한다:</p>
 * <ol>
 *   <li>공개 API 여부 확인 → 공개면 검증 없이 통과</li>
 *   <li>Authorization: Bearer {token} 헤더에서 토큰 추출</li>
 *   <li>JWT 검증 성공 → {@code X-User-Id}, {@code X-User-Email} 헤더를 하위 서비스에 주입</li>
 *   <li>JWT 검증 실패 → 401 JSON 응답 반환</li>
 * </ol>
 */
@Component
public class JwtAuthFilter {

    private final JwtProvider jwtProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 공개 API 목록 (JWT 검증 제외) */
    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint(HttpMethod.POST, "/api/users/signup"),
            new PublicEndpoint(HttpMethod.POST, "/api/users/login"),
            new PublicEndpoint(HttpMethod.GET, "/api/stocks/**"),
            new PublicEndpoint(HttpMethod.GET, "/api/community/posts"),
            new PublicEndpoint(HttpMethod.GET, "/api/community/posts/**")
    );

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * Gateway 라우트에 적용할 {@link HandlerFilterFunction}을 반환한다.
     *
     * @return JWT 인증 필터 함수
     */
    public HandlerFilterFunction<ServerResponse, ServerResponse> filter() {
        return (request, next) -> {
            // 공개 API는 검증 없이 통과
            if (isPublicApi(request)) {
                return next.handle(request);
            }

            // Authorization 헤더 추출
            String authHeader = request.headers().firstHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse();
            }

            // JWT 검증
            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token)) {
                return unauthorizedResponse();
            }

            // 검증 성공 → X-User-Id, X-User-Email 헤더 주입
            Long userId = jwtProvider.getUserId(token);
            String email = jwtProvider.getEmail(token);

            ServerRequest modified = ServerRequest.from(request)
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Email", email)
                    .build();

            return next.handle(modified);
        };
    }

    /** 공개 API인지 확인한다 (메서드 + 경로 패턴 매칭). */
    private boolean isPublicApi(ServerRequest request) {
        HttpMethod method = request.method();
        String path = request.uri().getPath();

        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(ep -> ep.method().equals(method) && pathMatcher.match(ep.pattern(), path));
    }

    /** 401 Unauthorized JSON 응답을 반환한다. */
    private ServerResponse unauthorizedResponse() throws Exception {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "status", 401,
                        "code", "UNAUTHORIZED",
                        "message", "인증이 필요합니다."
                ));
    }

    /** 공개 API 엔드포인트 정의 (HTTP 메서드 + Ant 패턴) */
    private record PublicEndpoint(HttpMethod method, String pattern) {
    }
}
