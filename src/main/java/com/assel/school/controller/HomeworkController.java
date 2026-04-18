package com.assel.school.controller;

import com.assel.school.model.Homework;
import com.assel.school.model.Student;
import com.assel.school.service.HomeworkService;
import com.assel.school.service.StudentService;
import com.assel.school.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String viewHomeworks(@RequestParam(required = false) Long studentId, Model model, Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTeacher = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        boolean isStudent = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        if (isStudent) {
            String username = auth.getName();
            List<Homework> homeworks = homeworkService.getStudentHomeworksByUsername(username);
            model.addAttribute("homeworks", homeworks);
        } else if ((isAdmin || isTeacher) && studentId != null) {
            List<Homework> homeworks = homeworkService.getStudentHomeworks(studentId);
            model.addAttribute("homeworks", homeworks);
            model.addAttribute("studentId", studentId);
        } else if (isAdmin || isTeacher) {
            model.addAttribute("homeworks", homeworkService.findAll());
        } else {
            model.addAttribute("homeworks", List.of());
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("username", auth != null ? auth.getName() : "Guest");

        if (isAdmin || isTeacher) {
            model.addAttribute("students", studentService.findAll());
        } else {
            model.addAttribute("students", List.of());
        }

        model.addAttribute("subjects", subjectService.findAll());
        return "homework";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String addHomework(@RequestParam Long studentId,
                              @RequestParam Long subjectId,
                              @RequestParam String description,
                              RedirectAttributes redirectAttributes) {
        try {
            homeworkService.addHomework(studentId, subjectId, description);
            redirectAttributes.addFlashAttribute("success", "Домашняя работа успешно добавлена и ученик уведомлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении ДЗ: " + e.getMessage());
        }
        return "redirect:/homework";
    }

    @PostMapping("/{id}/complete")
    public String completeHomework(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            homeworkService.completeHomework(id);
            redirectAttributes.addFlashAttribute("success", "Статус ДЗ обновлен на ВЫПОЛНЕНО!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/homework";
    }
}
