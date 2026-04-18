package com.assel.school.controller;

import com.assel.school.model.Rating;
import com.assel.school.model.User;
import com.assel.school.service.RatingService;
import com.assel.school.service.StudentService;
import com.assel.school.service.SubjectService;
import com.assel.school.service.TeacherService;
import com.assel.school.service.CustomUserDetailsService;
import com.assel.school.service.EmailService;
import com.assel.school.model.Student;
import com.assel.school.model.Subject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;

    public RatingController(RatingService ratingService,
                            StudentService studentService,
                            SubjectService subjectService,
                            TeacherService teacherService,
                            CustomUserDetailsService userDetailsService,
                            EmailService emailService) {
        this.ratingService = ratingService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isTeacher(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
    }

    private boolean isStudent(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
    }

    @GetMapping
    public String ratings(Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        boolean isTeacher = isTeacher(auth);
        boolean isStudent = isStudent(auth);

        List<Rating> ratings = null;

        if (isStudent && auth != null) {
            String username = auth.getName();
            ratings = ratingService.findByStudentUsername(username);
        } else if (isAdmin || isTeacher) {
            ratings = ratingService.findAll();
        } else {
            ratings = List.of();
        }

        model.addAttribute("ratings", ratings);

        if (isAdmin || isTeacher) {
            model.addAttribute("students", studentService.findAll());
        } else {
            model.addAttribute("students", List.of());
        }
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("teachers", teacherService.findAll());

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("username", auth != null ? auth.getName() : "Guest");

        return "ratings";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public String addRating(@RequestParam Long studentId,
                            @RequestParam Long subjectId,
                            @RequestParam Integer score,
                            @RequestParam(required = false) Long teacherId,
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        try {
            ratingService.addRating(studentId, subjectId, score, teacherId);

            Student student = studentService.findById(studentId).orElse(null);
            Subject subject = subjectService.findById(subjectId).orElse(null);

            if (student != null && student.getEmail() != null && !student.getEmail().isBlank() && subject != null) {
                try {
                    emailService.sendRatingNotificationEmail(student.getEmail(), student.getName(), subject.getName(), score.toString());
                    redirectAttributes.addFlashAttribute("successMessage", "Оценка успешно сохранена. Владельцу отправлено уведомление на почту.");
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("successMessage", "✅ Оценка сохранена, но возникла проблема с отправкой email-уведомления");
                }
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "✅ Оценка успешно сохранена!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + e.getMessage());
        }
        return "redirect:/ratings";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRating(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.deleteRating(id);
            redirectAttributes.addFlashAttribute("successMessage", "🗑 Оценка удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка удаления.");
        }
        return "redirect:/ratings";
    }
}


