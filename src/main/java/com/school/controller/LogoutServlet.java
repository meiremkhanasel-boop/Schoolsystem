package com.school.controller;

// 1. Нужно ОБЯЗАТЕЛЬНО добавить импорт для HttpSession
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// 2. Исправлена кавычка перед /logout
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 3. Оптимизирована логика удаления сессии
        HttpSession session = request.getSession(false); // Берем существующую, новую не создаем
        if (session != null) {
            session.invalidate(); // Убиваем сессию
        }

        // 4. Перенаправляем на сервлет логина
        response.sendRedirect("login");
    }
}