package com.vm.apigateway.security;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RouteAuthorizationEvaluator {

    public boolean isAuthorized(String requestPath, HttpMethod method, String role) {
        if ("ADMIN".equals(role)) {
            return true;
        }

        if (!"CUSTOMER".equals(role)) {
            return false;
        }

        if (("/api/auth/me".equals(requestPath) || "/auth/me".equals(requestPath)) && method == HttpMethod.GET) {
            return true;
        }

        if (("/api/customers".equals(requestPath) || "/patients".equals(requestPath)) && method == HttpMethod.POST) {
            return true;
        }

        if (("/api/customers/me".equals(requestPath) || "/patients/me".equals(requestPath))
                && (method == HttpMethod.GET || method == HttpMethod.PUT)) {
            return true;
        }

        if (requestPath.startsWith("/api/food-items")) {
            return method == HttpMethod.GET;
        }

        if (requestPath.startsWith("/api/orders")) {
            return method == HttpMethod.GET || method == HttpMethod.POST;
        }

        if (requestPath.startsWith("/api/payments")) {
            return method == HttpMethod.GET;
        }

        if (requestPath.startsWith("/api/notifications")) {
            return method == HttpMethod.GET;
        }

        return false;
    }
}
