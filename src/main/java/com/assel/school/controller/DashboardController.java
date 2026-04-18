package com.assel.school.controller;

import com.assel.school.service.StudentService;
import com.assel.school.service.TeacherService;
import com.assel.school.service.CustomUserDetailsService;
import com.assel.school.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CustomUserDetailsService userService;
    private final DataSource dataSource;

    public DashboardController(StudentService studentService,
                               TeacherService teacherService,
                               CustomUserDetailsService userService,
                               DataSource dataSource) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        try {
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isTeacher = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

            model.addAttribute("username", auth != null ? auth.getName() : "Admin");
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isTeacher", isTeacher);
            model.addAttribute("studentCount", studentService.count());
            model.addAttribute("teacherCount", teacherService.count());
            model.addAttribute("activeStudents", studentService.countByStatus("active"));
            model.addAttribute("activeTeachers", teacherService.countByStatus("active"));

            List<User> allUsers = userService.findAll();
            model.addAttribute("userCount", allUsers != null ? allUsers.size() : 0);
            model.addAttribute("adminCount",
                    allUsers != null ? allUsers.stream()
                            .filter(u -> "ADMIN".equals(u.getRole()))
                            .count() : 0);

            Map<String, Long> gradeStats = studentService.countByGrade();
            model.addAttribute("gradeStats", gradeStats != null ? gradeStats : new LinkedHashMap<>());

            Map<String, Long> subjectStats = teacherService.countBySubject();
            model.addAttribute("subjectStats", subjectStats != null ? subjectStats : new LinkedHashMap<>());

            List<?> recentStudents = studentService.findRecent(5);
            model.addAttribute("recentStudents", recentStudents != null ? recentStudents : new ArrayList<>());

            List<?> recentTeachers = teacherService.findRecent(5);
            model.addAttribute("recentTeachers", recentTeachers != null ? recentTeachers : new ArrayList<>());

            String dbStatus = "❌ Нет подключения";
            String dbType = "Неизвестно";
            String dbUrl = "";
            boolean dbOk = false;
            try (Connection conn = dataSource.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    dbOk = true;
                    dbStatus = "✅ Подключено";
                    try {
                        dbType = conn.getMetaData().getDatabaseProductName()
                                + " " + conn.getMetaData().getDatabaseProductVersion();
                        dbUrl = conn.getMetaData().getURL();
                    } catch (Exception e) {
                        log.warn("Warning getting DB metadata", e);
                    }
                }
            } catch (Exception e) {
                dbStatus = "❌ Ошибка: " + e.getMessage();
                log.error("Error connecting to database", e);
            }
            model.addAttribute("dbOk", dbOk);
            model.addAttribute("dbStatus", dbStatus);
            model.addAttribute("dbType", dbType);
            model.addAttribute("dbUrl", dbUrl);

            return "dashboard";
        } catch (Exception e) {
            log.error("Error in dashboard controller", e);
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ошибка при загрузке дашборда: " + e.getMessage());
            return "dashboard";
        }
    }

    /** API для графиков Chart.js */
    @GetMapping("/api/stats/grades")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> gradeStats() {
        try {
            Map<String, Long> stats = studentService.countByGrade();
            return ResponseEntity.ok(stats != null ? stats : new LinkedHashMap<>());
        } catch (Exception e) {
            log.error("Error getting grade stats", e);
            return ResponseEntity.ok(new LinkedHashMap<>());
        }
    }

    @GetMapping("/api/stats/subjects")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> subjectStats() {
        try {
            Map<String, Long> stats = teacherService.countBySubject();
            return ResponseEntity.ok(stats != null ? stats : new LinkedHashMap<>());
        } catch (Exception e) {
            log.error("Error getting subject stats", e);
            return ResponseEntity.ok(new LinkedHashMap<>());
        }
    }

    @GetMapping("/api/stats/summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> summary() {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("students", studentService.count());
            data.put("teachers", teacherService.count());
            data.put("activeStudents", studentService.countByStatus("active"));
            data.put("activeTeachers", teacherService.countByStatus("active"));

            List<User> allUsers = userService.findAll();
            data.put("users", allUsers != null ? allUsers.size() : 0);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error getting summary stats", e);
            return ResponseEntity.ok(new LinkedHashMap<>());
        }
    }
}
