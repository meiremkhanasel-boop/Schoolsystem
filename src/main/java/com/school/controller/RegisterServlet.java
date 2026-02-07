package com.school.controller;

import com.school.model.User;
import com.school.repository.SchoolData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {

    // doGet откроет страницу регистрации, если мы перейдем по ссылке /register
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    // doPost сработает, когда пользователь нажмет кнопку на форме регистрации
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("password");

        if (login != null && !login.isEmpty()) {
            SchoolData.addUser(new User(login, pass));

            // ВАЖНО: Удали строку с request.getSession().setAttribute...
            // И измени путь на login:
            response.sendRedirect("login");
        } else {
            response.sendRedirect("register.jsp?error=empty");
        }
    }
}