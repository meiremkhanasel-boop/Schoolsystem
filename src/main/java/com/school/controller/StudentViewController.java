package com.school.controller;

import com.school.model.Student;
import com.school.repository.SchoolData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentViewController {

    @GetMapping
    public String showStudents(Model model) {
        model.addAttribute("students", SchoolData.getStudents());
        return "students"; // Открывает файл src/main/resources/templates/students.html
    }

    @PostMapping("/add")
    public String addStudent(@RequestParam("id") Long id,
                             @RequestParam("name") String name,
                             @RequestParam("grade") String grade) {
        SchoolData.getStudents().add(new Student(id, name, grade));

        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id) {
        SchoolData.getStudents().removeIf(s -> s.getId().equals(id));

        return "redirect:/students";
    }
}