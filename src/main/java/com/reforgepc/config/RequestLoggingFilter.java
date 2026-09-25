package com.reforgepc.config;

import com.reforgepc.service.DiscordNotificationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_START_TIME =
            RequestLoggingFilter.class.getName() + ".START_TIME";

    private final DiscordNotificationService discordNotificationService;

    public RequestLoggingFilter(
            DiscordNotificationService discordNotificationService
    ) {
        this.discordNotificationService = discordNotificationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        request.setAttribute(REQUEST_START_TIME, startTime);

        try {
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            discordNotificationService.sendErrorLog(
                    request.getMethod(),
                    request.getRequestURI(),
                    getUsername(),
                    e,
                    duration
            );

            throw e;

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            if (shouldLog(request, response)) {
                discordNotificationService.sendRequestLog(
                        request.getMethod(),
                        request.getRequestURI(),
                        getUsername(),
                        response.getStatus(),
                        duration
                );
            }
        }
    }

    private boolean shouldLog(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        boolean modifyingRequest =
                method.equals("POST")
                        || method.equals("PUT")
                        || method.equals("PATCH")
                        || method.equals("DELETE");

        boolean authenticationRequest =
                uri.equals("/login")
                        || uri.equals("/logout");

        boolean passwordRequest =
                uri.startsWith("/account/change-password")
                        || uri.startsWith("/forgot-password");

        boolean errorResponse = status >= 400;

        return modifyingRequest
                || authenticationRequest
                || passwordRequest
                || errorResponse;
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

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/webjars/")
                || uri.equals("/favicon.ico");
    }
}