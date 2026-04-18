package com.assel.school.service;

import com.assel.school.model.Homework;
import com.assel.school.model.Student;
import com.assel.school.model.Subject;
import com.assel.school.repository.HomeworkRepository;
import com.assel.school.repository.StudentRepository;
import com.assel.school.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initTable() {
        try {
            String createTableSql =
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='homework' AND xtype='U') " +
                "CREATE TABLE homework (" +
                "id BIGINT IDENTITY(1,1) PRIMARY KEY, " +
                "description NVARCHAR(MAX), " +
                "status NVARCHAR(50), " +
                "created_at DATETIME DEFAULT GETDATE(), " +
                "student_id BIGINT, " +
                "subject_id BIGINT);";
            jdbcTemplate.execute(createTableSql);
            System.out.println("✅ Таблица 'homework' успешно проверена/создана в БД (SQL Server)");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании таблицы homework: " + e.getMessage());
        }
    }

    public Homework addHomework(Long studentId, Long subjectId, String description) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Subject subject = subjectRepository.findById(subjectId).orElse(null);

        if (student == null || subject == null) {
            throw new RuntimeException("Студент или предмет не найдены");
        }

        Homework homework = new Homework.HomeworkBuilder()
            .description(description)
            .subject(subject)
            .student(student)
            .status("НЕ ВЫПОЛНЕНО")
            .build();

        try {
            homeworkRepository.save(homework);

            if (homework.getStudent() != null && homework.getStudent().getEmail() != null) {
                emailService.sendEmail(
                    homework.getStudent().getEmail(),
                    "Новое домашнее задание",
                    "Вам назначено ДЗ: " + homework.getDescription()
                );
            }

        } catch (Exception e) {
            System.err.println("Ошибка при сохранении ДЗ или отправке email:");
            e.printStackTrace();
        }

        return homework;
    }

    public List<Homework> getStudentHomeworks(Long studentId) {
        return homeworkRepository.findByStudentId(studentId);
    }

    public List<Homework> getStudentHomeworksByUsername(String username) {
        return homeworkRepository.findByStudent_User_Username(username);
    }

    public List<Homework> findAll() {
        return homeworkRepository.findAll();
    }

    public Homework completeHomework(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new RuntimeException("Homework not found"));
        homework.setStatus("✅ ВЫПОЛНЕНО");
        return homeworkRepository.save(homework);
    }
}
