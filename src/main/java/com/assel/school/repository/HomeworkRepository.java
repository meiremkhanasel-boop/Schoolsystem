package com.assel.school.repository;

import com.assel.school.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByStudentId(Long studentId);

    List<Homework> findByStudent_User_Username(String username);
}

