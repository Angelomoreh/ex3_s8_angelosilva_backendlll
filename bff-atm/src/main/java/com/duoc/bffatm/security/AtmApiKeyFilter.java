package com.duoc.bffatm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AtmApiKeyFilter extends OncePerRequestFilter {

    private final byte[] apiKey;

    public AtmApiKeyFilter(@Value("${bff.atm.api-key}") String apiKey) {
        this.apiKey = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/atm/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String receivedKey = request.getHeader("X-ATM-KEY");
        boolean valid = receivedKey != null && MessageDigest.isEqual(
                apiKey,
                receivedKey.getBytes(StandardCharsets.UTF_8)
        );

        if (!valid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"estado\":401,\"mensaje\":\"Clave de cajero inválida o ausente\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
