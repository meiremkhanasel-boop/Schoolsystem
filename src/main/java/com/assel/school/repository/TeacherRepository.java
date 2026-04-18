package com.assel.school.repository;

import com.assel.school.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findBySubjectIgnoreCase(String subject);
    List<Teacher> findByNameContainingIgnoreCase(String name);
    List<Teacher> findByStatus(String status);
    long countByStatus(String status);
}
