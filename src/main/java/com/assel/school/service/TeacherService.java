package com.assel.school.service;

import com.assel.school.model.Teacher;
import com.assel.school.repository.TeacherRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public boolean deleteById(Long id) {
        if (teacherRepository.existsById(id)) {
            teacherRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean update(Long id, String name, String subject, String email, String phone, Integer experience, String status) {
        return teacherRepository.findById(id).map(t -> {
            t.setName(name);
            t.setSubject(subject);
            if (email != null && !email.isBlank()) t.setEmail(email);
            if (phone != null && !phone.isBlank()) t.setPhone(phone);
            if (experience != null) t.setExperience(experience);
            if (status != null && !status.isBlank()) t.setStatus(status);
            teacherRepository.save(t);
            return true;
        }).orElse(false);
    }

    public long count() {
        return teacherRepository.count();
    }

    public long countByStatus(String status) {
        return teacherRepository.countByStatus(status);
    }

    public List<Teacher> searchByName(String name) {
        return teacherRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Teacher> findBySubject(String subject) {
        return teacherRepository.findBySubjectIgnoreCase(subject);
    }

    public List<Teacher> findRecent(int limit) {
        return teacherRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
    }

    public Map<String, Long> countBySubject() {
        return teacherRepository.findAll().stream()
                .collect(Collectors.groupingBy(Teacher::getSubject, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<Teacher> findByStatus(String status) {
        return teacherRepository.findByStatus(status);
    }

    public long countDistinctSubjects() {
        return teacherRepository.findAll().stream()
                .map(Teacher::getSubject)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .count();
    }
}
