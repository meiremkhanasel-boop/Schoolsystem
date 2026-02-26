package com.school.filter;

import com.school.util.JwtTokenProvider;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/api/students/*", "/api/teachers/*", "/api/logout"})
public class AuthFilter implements Filter {
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\": \"Токен жоқ. Authorization: Bearer <token> қойыңыз\"}");
            return;
        }

        String token = authHeader.substring(7);

        String username = jwtTokenProvider.getUsernameFromToken(token);
        if (username == null || jwtTokenProvider.isTokenExpired(token)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\": \"Өндік емес немесе ақталған токен\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}