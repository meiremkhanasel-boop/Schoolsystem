package com.assel.school.service;

import com.assel.school.model.Rating;
import com.assel.school.model.Student;
import com.assel.school.model.Subject;
import com.assel.school.model.Teacher;
import com.assel.school.repository.RatingRepository;
import com.assel.school.repository.StudentRepository;
import com.assel.school.repository.SubjectRepository;
import com.assel.school.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final EmailService emailService;

    public RatingService(RatingRepository ratingRepository,
                         StudentRepository studentRepository,
                         SubjectRepository subjectRepository,
                         TeacherRepository teacherRepository,
                         EmailService emailService) {
        this.ratingRepository = ratingRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.emailService = emailService;
    }

    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    public List<Rating> findByStudent(Long studentId) {
        return ratingRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    public List<Rating> findByStudentUsername(String username) {
        return ratingRepository.findByStudent_User_UsernameOrderByDateDesc(username);
    }

    public void addRating(Long studentId, Long subjectId, Integer score, Long teacherId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));

        Teacher teacher = null;
        if (teacherId != null) {
            teacher = teacherRepository.findById(teacherId).orElse(null);
        }

        if (score < 2 || score > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 2 до 5");
        }

        // Используем Builder паттерн
        Rating rating = new Rating.RatingBuilder(student, subject, score)
                .teacher(teacher)
                .date(LocalDate.now())
                .build();

        ratingRepository.save(rating);

        if (student.getEmail() != null && !student.getEmail().isBlank()) {
            try {
                emailService.sendRatingNotificationEmail(student.getEmail(), student.getName(), subject.getName(), String.valueOf(score));
            } catch (Exception e) {
            }
        }
    }

    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }

    public double getAverageRating(Long studentId) {
        List<Rating> ratings = ratingRepository.findByStudentIdOrderByDateDesc(studentId);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }

    public long count() {
        return ratingRepository.count();
    }
}
