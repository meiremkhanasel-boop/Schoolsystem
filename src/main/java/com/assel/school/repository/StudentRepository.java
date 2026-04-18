package com.assel.school.repository;

import com.assel.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGrade(String grade);
    List<Student> findByNameContainingIgnoreCase(String name);
    java.util.Optional<Student> findByName(String name);
    List<Student> findByStatus(String status);
    long countByStatus(String status);
}
