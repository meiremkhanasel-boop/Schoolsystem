package com.school.controller;

import com.school.model.Teacher;
import com.school.repository.SchoolData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
public class TeacherViewController {

    @GetMapping
    public String showTeachersPage(Model model) {
        model.addAttribute("teachers", SchoolData.getTeachers());
        return "teachers";
    }

    @PostMapping("/add")
    public String addTeacher(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam String subject) {
        SchoolData.getTeachers().add(new Teacher(id, name, subject));
        return "redirect:/teachers"; // Перезагружаем страницу, чтобы увидеть нового учителя
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id) {
        SchoolData.getTeachers().removeIf(t -> t.getId().equals(id));
        return "redirect:/teachers";
    }
}