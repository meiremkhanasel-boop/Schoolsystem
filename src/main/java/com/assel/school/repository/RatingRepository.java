package com.assel.school.repository;

import com.assel.school.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByStudentIdOrderByDateDesc(Long studentId);
    List<Rating> findBySubjectId(Long subjectId);
    List<Rating> findByTeacherId(Long teacherId);
    List<Rating> findByDateBetween(LocalDate start, LocalDate end);

    List<Rating> findByStudent_User_UsernameOrderByDateDesc(String username);
}

