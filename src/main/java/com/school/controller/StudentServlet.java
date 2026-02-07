package com.school.controller;

import com.school.model.Student;
import com.school.repository.SchoolData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet(name = "StudentServlet", value = "/students")
public class StudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Читаем данные из полей формы
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String name = request.getParameter("name");
            String grade = request.getParameter("grade");

            // Создаем объект и сохраняем в SchoolData
            SchoolData.getStudents().add(new Student(id, name, grade));
        } catch (Exception e) {
            e.printStackTrace(); // Выведет ошибку в консоль IDEA, если ID — не число
        }
        // Обновляем страницу, чтобы увидеть нового студента
        response.sendRedirect("students");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        if ("delete".equals(action) && idStr != null) {
            Long id = Long.parseLong(idStr);
            // Удаляем студента по ID
            SchoolData.getStudents().removeIf(s -> s.getId().equals(id));
        }

        request.setAttribute("studentsList", SchoolData.getStudents());
        request.getRequestDispatcher("/students.jsp").forward(request, response);
    }
}