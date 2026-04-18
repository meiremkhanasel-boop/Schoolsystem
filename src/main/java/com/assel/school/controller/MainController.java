package com.assel.school.controller;

import com.assel.school.model.Student;
import com.assel.school.model.Teacher;
import com.assel.school.repository.UserRepository;
import com.assel.school.service.StudentService;
import com.assel.school.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/main")
public class MainController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MainController(StudentService studentService, TeacherService teacherService,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.studentService  = studentService;
        this.teacherService  = teacherService;
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<String> printMessage() {
        return ResponseEntity.ok("School Management System API — работает! Студентов: "
                + studentService.count() + ", Учителей: " + teacherService.count());
    }


    @GetMapping("/fix-admin")
    public ResponseEntity<Map<String, Object>> fixAdmin() {
        Map<String, Object> result = new LinkedHashMap<>();

        fixUser("admin", "admin123", "ADMIN", result);
        fixUser("user", "user123", "USER", result);

        result.put("status", "OK");
        result.put("message", "Готово! Войдите: admin/admin123 или user/user123");
        return ResponseEntity.ok(result);
    }

    private void fixUser(String username, String rawPass, String role, Map<String, Object> result) {
        userRepository.findByUsername(username).ifPresentOrElse(u -> {
            String stored  = u.getPassword() == null ? "" : u.getPassword();
            boolean isOk   = (stored.startsWith("$2a$") || stored.startsWith("$2b$"))
                              && passwordEncoder.matches(rawPass, stored);
            if (!isOk) {
                u.setPassword(passwordEncoder.encode(rawPass));
                u.setRole(role);
                userRepository.save(u);
                result.put(username, "FIXED — пароль перезашифрован → " + rawPass);
            } else {
                result.put(username, "OK — пароль правильный");
            }
        }, () -> {
            com.assel.school.model.User u = new com.assel.school.model.User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(rawPass));
            u.setRole(role);
            userRepository.save(u);
            result.put(username, "CREATED — создан с паролем " + rawPass);
        });
    }

    @GetMapping("/special")
    public ResponseEntity<Student> getSpecialStudent() {
        return studentService.findAll().stream().findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/students/create")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student saved = studentService.save(student);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/teachers/create")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        Teacher saved = teacherService.save(teacher);
        return ResponseEntity.ok(saved);
    }
}


