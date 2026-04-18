package com.assel.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String status = "НЕ ВЫПОЛНЕНО";

    private LocalDateTime createdAt = LocalDateTime.now();

    public Homework() {}

    private Homework(HomeworkBuilder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.subject = builder.subject;
        this.student = builder.student;
        if (builder.status != null) {
            this.status = builder.status;
        }
        if (builder.createdAt != null) {
            this.createdAt = builder.createdAt;
        }
    }

    public static class HomeworkBuilder {
        private Long id;
        private String description;
        private Subject subject;
        private Student student;
        private String status;
        private LocalDateTime createdAt;

        public HomeworkBuilder(Student student, Subject subject, String description) {
            if (student == null) throw new IllegalArgumentException("Student is required");
            if (subject == null) throw new IllegalArgumentException("Subject is required");
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Description is required");
            }
            this.student = student;
            this.subject = subject;
            this.description = description;
        }


        public HomeworkBuilder() {}

        public HomeworkBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HomeworkBuilder description(String description) {
            this.description = description;
            return this;
        }

        public HomeworkBuilder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public HomeworkBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public HomeworkBuilder status(String status) {
            this.status = status;
            return this;
        }

        public HomeworkBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Homework build() {
            return new Homework(this);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
