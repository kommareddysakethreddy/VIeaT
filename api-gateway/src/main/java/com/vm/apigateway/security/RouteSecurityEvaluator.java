package com.vm.apigateway.security;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RouteSecurityEvaluator {

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/api/auth/login",
            "/api/auth/register",
            // Gateway route rewriting can expose the downstream auth-service path inside
            // later filters, so keep both the public and rewritten variants public.
            "/auth/login",
            "/auth/register",
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui",
            "/v3/api-docs",
            "/openapi.yml"
    );

    public boolean requiresAuthentication(String requestPath) {
        return PUBLIC_PATH_PREFIXES.stream().noneMatch(requestPath::startsWith);
    }
}
