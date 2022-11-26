package com.explosion204.wclookup.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class JwtFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_HEADER = "Access-Token";

    private final AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);

        if (accessToken != null) {
            Authentication authentication = new JwtAuthentication(accessToken);
            authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(SC_UNAUTHORIZED);
            }
        } else {
            response.sendError(SC_UNAUTHORIZED);
        }
    }
}
