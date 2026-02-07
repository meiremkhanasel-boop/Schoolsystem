package com.school.controller;

import com.school.model.User;
import com.school.repository.SchoolData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    private Object login;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
        request.getSession().setAttribute("user", login);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("password");

        // Ищем пользователя в нашем списке
        boolean userExists = SchoolData.getUsers().stream()
                .anyMatch(u -> u.getLogin().equals(login) && u.getPassword().equals(pass));

        if (userExists) {
            // Создаем сессию — это "пропуск", который браузер будет хранить
            HttpSession session = request.getSession();
            session.setAttribute("user", login);
            response.sendRedirect("index.jsp");
        } else {
            // Если пароль неверный, возвращаем на логин с ошибкой
            response.sendRedirect("login.jsp?error=invalid");
        }
    }
}