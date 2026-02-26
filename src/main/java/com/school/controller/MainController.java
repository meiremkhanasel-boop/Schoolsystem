package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.model.Student;
import com.school.model.Teacher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/main")
public class MainController {

    @GetMapping
    public ResponseEntity<String> printMessage() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/special")
    public ResponseEntity<Student> getSpecialStudent() {
        Student student = new Student(1L, "Мейремхан Асель", "10-А");
        return ResponseEntity.ok(student);
    }

    @PostMapping("/special")
    public ResponseEntity<Student> createStudentWithName(@RequestParam String name) {
        Student student = new Student(1L, name, "10-А");
        return ResponseEntity.ok(student);
    }

    @PostMapping("/json-test")
    public ResponseEntity<String> testJsonSerialization(@RequestBody Student student) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(student);
            return ResponseEntity.ok("Сериализацированная JSON: " + json);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/students/search")
    public ResponseEntity<List<Student>> searchStudentsByGrade(@RequestParam String grade) {
        List<Student> students = new ArrayList<>();
        students.add(new Student(1L, "Асель Мейремхан", grade));
        students.add(new Student(2L, "Жансая Абдикова", grade));
        students.add(new Student(3L, "Нурлан Сейткали", grade));
        return ResponseEntity.ok(students);
    }

    @PostMapping("/students/create")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        if (student.getId() == null) {
            student.setId(System.currentTimeMillis());
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = new Student(id, "Студент #" + id, "10-А");
        return ResponseEntity.ok(student);
    }

    @PostMapping("/teachers/create")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        if (teacher.getId() == null) {
            teacher.setId(System.currentTimeMillis());
        }
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/teachers/search")
    public ResponseEntity<List<Teacher>> searchTeachersBySubject(@RequestParam String subject) {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(1L, "Айгерим Токова", subject));
        teachers.add(new Teacher(2L, "Данияр Усенов", subject));
        return ResponseEntity.ok(teachers);
    }
}

