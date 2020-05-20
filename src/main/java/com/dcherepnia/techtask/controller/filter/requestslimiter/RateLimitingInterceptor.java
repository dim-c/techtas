package com.dcherepnia.techtask.controller.filter.requestslimiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpMethod.POST;


@Slf4j
@Component
public class RateLimitingInterceptor extends HandlerInterceptorAdapter {

    @Value("${rate.limit.enabled}")
    private boolean enabled;

    @Value("${rate.limit.rate.limit.per.second}")
    private int permits;

    private Map<String, SimpleRateLimiter> limiters = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!request.getMethod().equals(POST.name())) {
            return true;
        }
        if (!enabled) {
            return true;
        }
        String clientId = request.getHeader("Country");

        if (clientId == null) {
            return true;
        }
        SimpleRateLimiter rateLimiter = getRateLimiter(clientId);
        boolean allowRequest = rateLimiter.tryAcquire();

        if (!allowRequest) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        response.addHeader("X-RateLimit-Limit", String.valueOf(permits));
        return allowRequest;
    }

    private SimpleRateLimiter getRateLimiter(String clientId) {
        if (limiters.containsKey(clientId)) {
            return limiters.get(clientId);
        } else {
            synchronized (clientId.intern()) {
                // double-checked locking to avoid multiple-reinitializations
                if (limiters.containsKey(clientId)) {
                    return limiters.get(clientId);
                }

                SimpleRateLimiter rateLimiter = SimpleRateLimiter.create(permits, TimeUnit.HOURS);

                limiters.put(clientId, rateLimiter);
                return rateLimiter;
            }
        }
    }

    @PreDestroy
    public void destroy() {
        limiters.forEach((s, simpleRateLimiter) -> simpleRateLimiter.stop());
        limiters.clear();
    }
}
