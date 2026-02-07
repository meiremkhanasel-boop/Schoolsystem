package com.school.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Применяем фильтр к защищенным разделам
@WebFilter(urlPatterns = {"/students", "/teachers", "/index.jsp", "/"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String loginURI = req.getContextPath() + "/login";
        String registerURI = req.getContextPath() + "/register";

        // Проверяем, залогинен ли пользователь
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        // Проверяем, не пытается ли пользователь СЕЙЧАС зайти на страницу логина или регистрации
        boolean loginRequest = req.getRequestURI().equals(loginURI);
        boolean registerRequest = req.getRequestURI().equals(registerURI);

        if (loggedIn || loginRequest || registerRequest) {
            // Если залогинен ИЛИ это страница входа/регистрации — пускаем дальше
            chain.doFilter(request, response);
        } else {
            // Если не залогинен и пытается зайти на защищенную страницу — на логин
            res.sendRedirect(loginURI);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}