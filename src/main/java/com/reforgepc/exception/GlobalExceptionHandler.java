package com.reforgepc.exception;

import com.reforgepc.config.RequestLoggingFilter;
import com.reforgepc.service.DiscordNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final DiscordNotificationService discordNotificationService;

    public GlobalExceptionHandler(
            DiscordNotificationService discordNotificationService
    ) {
        this.discordNotificationService = discordNotificationService;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        long duration = getRequestDuration(request);

        discordNotificationService.sendErrorLog(
                request.getMethod(),
                request.getRequestURI(),
                getUsername(),
                exception,
                duration
        );

        return "error/error";
    }

    private long getRequestDuration(HttpServletRequest request) {
        Object startTime =
                request.getAttribute(RequestLoggingFilter.REQUEST_START_TIME);

        if (startTime instanceof Long) {
            return System.currentTimeMillis() - (Long) startTime;
        }

        return 0;
    }

    private String getUsername() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "anonymous";
        }

        return authentication.getName();
    }
}