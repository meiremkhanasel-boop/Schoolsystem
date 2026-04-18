package com.assel.school.service;

import com.assel.school.model.Student;
import com.assel.school.repository.StudentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public boolean deleteById(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean update(Long id, String name, String grade, String email, String phone, String status) {
        return studentRepository.findById(id).map(s -> {
            s.setName(name);
            s.setGrade(grade);
            if (email != null && !email.isBlank()) s.setEmail(email);
            if (phone != null && !phone.isBlank()) s.setPhone(phone);
            if (status != null && !status.isBlank()) s.setStatus(status);
            studentRepository.save(s);
            return true;
        }).orElse(false);
    }

    public long count() {
        return studentRepository.count();
    }

    public long countByStatus(String status) {
        return studentRepository.countByStatus(status);
    }

    public List<Student> findByGrade(String grade) {
        return studentRepository.findByGrade(grade);
    }

    public List<Student> searchByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByName(username);
    }

    public List<Student> findRecent(int limit) {
        return studentRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
    }

    public Map<String, Long> countByGrade() {
        return studentRepository.findAll().stream()
                .collect(Collectors.groupingBy(Student::getGrade, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<Student> findByStatus(String status) {
        return studentRepository.findByStatus(status);
    }
}
