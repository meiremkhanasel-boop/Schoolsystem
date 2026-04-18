package com.assel.school.controller;

import com.assel.school.model.Student;
import com.assel.school.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return studentService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStudent(@RequestBody Student student) {
        if (student.getName() == null || student.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Имя студента обязательно"));
        }
        Student saved = studentService.save(student);
        return ResponseEntity.ok(Map.of("success", true, "message", "Студент добавлен", "student", saved));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id,
                                                              @RequestBody Student student) {
        if (student.getName() == null || student.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Имя студента обязательно"));
        }
        boolean updated = studentService.update(id, student.getName(), student.getGrade(),
                student.getEmail(), student.getPhone(), student.getStatus());
        if (updated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Данные студента обновлены"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        if (studentService.deleteById(id)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Студент удалён"));
        }
        return ResponseEntity.notFound().build();
    }
}