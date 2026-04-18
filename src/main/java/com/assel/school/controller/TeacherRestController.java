package com.assel.school.controller;

import com.assel.school.model.Teacher;
import com.assel.school.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {

    private final TeacherService teacherService;

    public TeacherRestController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Long id) {
        return teacherService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addTeacher(@RequestBody Teacher teacher) {
        if (teacher.getName() == null || teacher.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Имя учителя обязательно"));
        }
        Teacher saved = teacherService.save(teacher);
        return ResponseEntity.ok(Map.of("success", true, "message", "Учитель добавлен", "teacher", saved));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable Long id,
                                                              @RequestBody Teacher teacher) {
        if (teacher.getName() == null || teacher.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Имя учителя обязательно"));
        }
        boolean updated = teacherService.update(id, teacher.getName(), teacher.getSubject(),
                teacher.getEmail(), teacher.getPhone(), teacher.getExperience(), teacher.getStatus());
        if (updated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Данные преподавателя обновлены"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long id) {
        if (teacherService.deleteById(id)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Учитель удалён"));
        }
        return ResponseEntity.notFound().build();
    }
}