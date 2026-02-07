package com.school.controller;

import com.school.model.Teacher;
import com.school.repository.SchoolData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/teachers")
public class TeacherServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // Получаем данные
            String idStr = request.getParameter("id");
            String name = request.getParameter("name");
            String subject = request.getParameter("subject");

            if (idStr != null && !idStr.isEmpty()) {
                Long id = Long.parseLong(idStr.trim());
                // Добавляем учителя в список
                SchoolData.getTeachers().add(new Teacher(id, name, subject));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку, если ID пришел не числом
        }

        // ИСПРАВЛЕНО: Правильный редирект (было sendRedirresponse...)
        response.sendRedirect(request.getContextPath() + "/teachers");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        if ("delete".equals(action) && idStr != null) {
            try {
                Long id = Long.parseLong(idStr);
                SchoolData.getTeachers().removeIf(t -> t.getId().equals(id));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("teachersList", SchoolData.getTeachers());

        // ИСПРАВЛЕНО: Название файла (было teashers.jsp через 's')
        request.getRequestDispatcher("/teachers.jsp").forward(request, response);
    }
}