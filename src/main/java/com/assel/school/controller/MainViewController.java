package com.assel.school.controller;

import com.assel.school.model.Student;
import com.assel.school.model.Teacher;
import com.assel.school.service.StudentService;
import com.assel.school.service.TeacherService;
import com.assel.school.service.SubjectService;
import com.assel.school.service.RatingService;
import com.assel.school.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MainViewController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final RatingService ratingService;
    private final CustomUserDetailsService userService;

    public MainViewController(StudentService studentService,
                               TeacherService teacherService,
                               SubjectService subjectService,
                               RatingService ratingService,
                               CustomUserDetailsService userService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.ratingService = ratingService;
        this.userService = userService;
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

    @GetMapping("/")
    public String index(Model model, Authentication auth) {
        model.addAttribute("studentCount", studentService.count());
        model.addAttribute("teacherCount", teacherService.count());
        model.addAttribute("subjectCount", subjectService.count());
        model.addAttribute("ratingCount", ratingService.count());

        String username = "Guest";
        boolean isAdmin = false;
        boolean isTeacher = false;
        boolean isStudent = false;

        if (auth != null && auth.getName() != null) {
            username = auth.getName();
            isAdmin = isAdmin(auth);
            isTeacher = isTeacher(auth);
            isStudent = isStudent(auth);
        }

        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isStudent", isStudent);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model, Authentication auth) {
        if (auth != null) model.addAttribute("username", auth.getName());
        return "access-denied";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth, HttpSession session) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        model.addAttribute("isAdmin", isAdmin(auth));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        long createdAt = session.getCreationTime();
        String loginTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()).format(fmt);

        long lastAccess = session.getLastAccessedTime();
        String lastAccessTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastAccess), ZoneId.systemDefault()).format(fmt);

        int maxInactive = session.getMaxInactiveInterval(); // секунды
        String expireIn = (maxInactive / 60) + " мин";

        String sessionId = session.getId();
        String shortSessionId = sessionId.length() > 8
                ? sessionId.substring(0, 8) + "..." : sessionId;

        model.addAttribute("loginTime", loginTime);
        model.addAttribute("lastAccessTime", lastAccessTime);
        model.addAttribute("sessionExpireIn", expireIn);
        model.addAttribute("sessionId", shortSessionId);

        return "profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication auth,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("passError", "❌ Новый пароль и подтверждение не совпадают");
            return "redirect:/profile";
        }
        if (newPassword.length() < 4) {
            ra.addFlashAttribute("passError", "❌ Пароль должен быть не менее 4 символов");
            return "redirect:/profile";
        }
        boolean ok = userService.changePassword(auth.getName(), oldPassword, newPassword);
        if (ok) {
            ra.addFlashAttribute("passSuccess", "✅ Пароль успешно изменён!");
        } else {
            ra.addFlashAttribute("passError", "❌ Старый пароль введён неверно");
        }
        return "redirect:/profile";
    }


    @GetMapping("/students")
    public String studentsPage(Model model, Authentication auth,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String grade) {
        boolean isStudentRole = isStudent(auth);

        List<Student> students = List.of();

        if (!isStudentRole) {
            if (search != null && !search.isBlank()) {
                students = studentService.searchByName(search);
                model.addAttribute("search", search);
            } else if (grade != null && !grade.isBlank()) {
                students = studentService.findByGrade(grade);
                model.addAttribute("filterGrade", grade);
            } else {
                students = studentService.findAll();
            }
        }

        model.addAttribute("students", students);
        model.addAttribute("allGrades", studentService.findAll().stream()
                .map(Student::getGrade).distinct().sorted().toList());
        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", isAdmin(auth));
        model.addAttribute("isTeacher", isTeacher(auth));
        model.addAttribute("isStudent", isStudentRole);
        return "students";
    }

    @PostMapping("/students/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addStudent(@RequestParam String name,
                             @RequestParam String grade,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String phone,
                             RedirectAttributes redirectAttributes) {
        Student student = new Student.StudentBuilder()
            .name(name.trim())
            .grade(grade.trim())
            .status("active")
            .build();
        if (email != null && !email.isBlank()) student.setEmail(email.trim());
        if (phone != null && !phone.isBlank()) student.setPhone(phone.trim());
        studentService.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Студент успешно добавлен!");
        return "redirect:/students";
    }

    @PostMapping("/students/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editStudent(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String grade,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String status,
                              RedirectAttributes redirectAttributes) {
        boolean updated = studentService.update(id, name.trim(), grade.trim(), email, phone, status);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Данные студента обновлены!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Студент с ID " + id + " не найден.");
        }
        return "redirect:/students";
    }

    @GetMapping("/students/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (studentService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "🗑 Студент удалён.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Студент не найден.");
        }
        return "redirect:/students";
    }


    @GetMapping("/teachers")
    public String teachersPage(Model model, Authentication auth,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String subject) {
        List<Teacher> teachers;
        if (search != null && !search.isBlank()) {
            teachers = teacherService.searchByName(search);
            model.addAttribute("search", search);
        } else if (subject != null && !subject.isBlank()) {
            teachers = teacherService.findBySubject(subject);
            model.addAttribute("filterSubject", subject);
        } else {
            teachers = teacherService.findAll();
        }
        model.addAttribute("teachers", teachers);
        model.addAttribute("allSubjects", teacherService.findAll().stream()
                .map(Teacher::getSubject).distinct().sorted().toList());
        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", isAdmin(auth));
        model.addAttribute("isTeacher", isTeacher(auth));
        model.addAttribute("isStudent", isStudent(auth));
        return "teachers";
    }

    @PostMapping("/teachers/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTeacher(@RequestParam String name,
                             @RequestParam String subject,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) Integer experience,
                             RedirectAttributes redirectAttributes) {
        Teacher teacher = new Teacher(name.trim(), subject.trim());
        if (email != null && !email.isBlank()) teacher.setEmail(email.trim());
        if (phone != null && !phone.isBlank()) teacher.setPhone(phone.trim());
        if (experience != null) teacher.setExperience(experience);
        teacherService.save(teacher);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Преподаватель успешно добавлен!");
        return "redirect:/teachers";
    }

    @PostMapping("/teachers/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editTeacher(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String subject,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) Integer experience,
                              @RequestParam(required = false) String status,
                              RedirectAttributes redirectAttributes) {
        boolean updated = teacherService.update(id, name.trim(), subject.trim(), email, phone, experience, status);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "✅ Данные преподавателя обновлены!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Преподаватель не найден.");
        }
        return "redirect:/teachers";
    }

    @GetMapping("/teachers/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (teacherService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "🗑 Преподаватель удалён.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Преподаватель не найден.");
        }
        return "redirect:/teachers";
    }
}
