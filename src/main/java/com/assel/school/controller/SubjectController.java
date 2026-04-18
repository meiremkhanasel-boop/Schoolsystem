package com.assel.school.controller;

import com.assel.school.model.Subject;
import com.assel.school.service.SubjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public String getAllSubjects(Model model, Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTeacher = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("username", auth != null ? auth.getName() : "Guest");

        model.addAttribute("subjects", subjectService.findAll());
        return "subjects";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addSubject(@RequestParam String name,
                             @RequestParam(required = false) String description,
                             RedirectAttributes redirectAttributes) {
        try {
            Subject s = new Subject.SubjectBuilder()
                .name(name.trim())
                .description(description)
                .build();
            subjectService.save(s);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Предмет успешно добавлен!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        return "redirect:/subjects";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editSubject(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              RedirectAttributes redirectAttributes) {
        try {
            subjectService.update(id, name.trim(), description);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Предмет обновлен!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        return "redirect:/subjects";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            subjectService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "🗑 Предмет удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка при удалении предмета.");
        }
        return "redirect:/subjects";
    }
}
