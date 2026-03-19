package com.starlit.gateway.config;

import com.starlit.gateway.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Gateway 라우팅 설정.
 *
 * <p>모든 라우트에 JWT 인증 필터를 적용한다.</p>
 *
 * <pre>
 * /api/users/**     → user-service (8001)
 * /api/stocks/**    → stock-service (8002)
 * /api/community/** → community-service (8003)
 * </pre>
 */
@Configuration
public class RouteConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public RouteConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        HandlerFilterFunction<ServerResponse, ServerResponse> jwt = jwtAuthFilter.filter();

        return route("user-service")
                .route(path("/api/users/**"), http())
                .before(uri("http://localhost:8001"))
                .filter(jwt)
                .build()
                .and(
                        route("stock-service")
                                .route(path("/api/stocks/**"), http())
                                .before(uri("http://localhost:8002"))
                                .filter(jwt)
                                .build()
                )
                .and(
                        route("community-service")
                                .route(path("/api/community/**"), http())
                                .before(uri("http://localhost:8003"))
                                .filter(jwt)
                                .build()
                );
    }
}
