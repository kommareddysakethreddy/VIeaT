package com.vm.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String method = String.valueOf(exchange.getRequest().getMethod());
        String path = exchange.getRequest().getURI().getRawPath();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                    long durationMs = System.currentTimeMillis() - startTime;
                    log.info("Gateway {} {} -> {} in {} ms",
                            method,
                            path,
                            statusCode != null ? statusCode.value() : "UNKNOWN",
                            durationMs);
                });
    }

    @Override
    public int getOrder() {
        // Run late so route filters can adjust the request before we log the outcome.
        return Ordered.LOWEST_PRECEDENCE;
    }
}
