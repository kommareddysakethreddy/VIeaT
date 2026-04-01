package com.vm.apigateway.filter;

import com.vm.apigateway.security.GatewayJwtService;
import com.vm.apigateway.security.RouteAuthorizationEvaluator;
import com.vm.apigateway.security.RouteSecurityEvaluator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final RouteSecurityEvaluator routeSecurityEvaluator;
    private final RouteAuthorizationEvaluator routeAuthorizationEvaluator;
    private final GatewayJwtService gatewayJwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(RouteSecurityEvaluator routeSecurityEvaluator,
                                   RouteAuthorizationEvaluator routeAuthorizationEvaluator,
                                   GatewayJwtService gatewayJwtService,
                                   ObjectMapper objectMapper) {
        this.routeSecurityEvaluator = routeSecurityEvaluator;
        this.routeAuthorizationEvaluator = routeAuthorizationEvaluator;
        this.gatewayJwtService = gatewayJwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        if (!routeSecurityEvaluator.requiresAuthentication(requestPath)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.info("Rejected unauthenticated request to {}", requestPath);
            return writeUnauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        try {
            Claims claims = gatewayJwtService.validateAndExtractClaims(token);
            String role = String.valueOf(claims.get("role"));
            if (!routeAuthorizationEvaluator.isAuthorized(requestPath, exchange.getRequest().getMethod(), role)) {
                log.info("Rejected forbidden request to {} for role={}", requestPath, role);
                return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
            }

            ServerHttpRequest authenticatedRequest = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {
                        sanitizeTrustedHeaders(headers);
                        // Forwarding selected claims keeps downstream services decoupled from JWT parsing.
                        headers.set("X-Authenticated-User", claims.getSubject());
                        headers.set("X-User-Role", role);
                        headers.set("X-User-Id", String.valueOf(claims.get("userId")));
                        addIfPresent(headers, "X-Customer-Id", claims.get("customerId"));
                    })
                    .build();

            return chain.filter(exchange.mutate().request(authenticatedRequest).build());
        } catch (JwtException | IllegalArgumentException ex) {
            log.info("Rejected request to {} due to invalid JWT: {}", requestPath, ex.getMessage());
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    @Override
    public int getOrder() {
        // Run before routing so unauthorized requests are rejected early.
        return -100;
    }

    private void sanitizeTrustedHeaders(HttpHeaders headers) {
        headers.remove("X-Authenticated-User");
        headers.remove("X-User-Role");
        headers.remove("X-User-Id");
        headers.remove("X-Customer-Id");
    }

    private void addIfPresent(HttpHeaders headers, String name, Object value) {
        if (value != null && !"null".equals(String.valueOf(value))) {
            headers.set(name, String.valueOf(value));
        }
    }

    private Mono<Void> writeUnauthorizedResponse(ServerWebExchange exchange, String message) {
        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", OffsetDateTime.now().toString());
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("message", message);
        responseBody.put("path", exchange.getRequest().getURI().getPath());

        byte[] responseBytes = toJson(responseBody).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String toJson(Map<String, Object> responseBody) {
        try {
            return objectMapper.writeValueAsString(responseBody);
        } catch (JsonProcessingException ex) {
            return "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Request failed\"}";
        }
    }
}
