package com.assel.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @NotNull(message = "Оценка обязательна")
    @Min(value = 2, message = "Оценка не может быть ниже 2")
    @Max(value = 5, message = "Оценка не может быть выше 5")
    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    public Rating() {}

    public Rating(Student student, Subject subject, Integer score, Teacher teacher) {
        this.student = student;
        this.subject = subject;
        this.score = score;
        this.teacher = teacher;
        this.date = LocalDate.now();
    }

    private Rating(RatingBuilder builder) {
        this.id = builder.id;
        this.student = builder.student;
        this.subject = builder.subject;
        this.teacher = builder.teacher;
        this.score = builder.score;
        this.date = builder.date != null ? builder.date : LocalDate.now();
    }

    public static class RatingBuilder {
        private Long id;
        private Student student;
        private Subject subject;
        private Teacher teacher;
        private Integer score;
        private LocalDate date;

        public RatingBuilder(Student student, Subject subject, Integer score) {
            if (student == null) throw new IllegalArgumentException("Student is required");
            if (subject == null) throw new IllegalArgumentException("Subject is required");
            if (score == null || score < 2 || score > 5) {
                throw new IllegalArgumentException("Score must be between 2 and 5");
            }
            this.student = student;
            this.subject = subject;
            this.score = score;
        }


        public RatingBuilder() {}

        public RatingBuilder id(Long id) { this.id = id; return this; }
        public RatingBuilder teacher(Teacher teacher) { this.teacher = teacher; return this; }
        public RatingBuilder date(LocalDate date) { this.date = date; return this; }

        public Rating build() {
            return new Rating(this);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}

