package com.assel.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя студента не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Класс не может быть пустым")
    @Column(nullable = false)
    private String grade;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String status = "active";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Student() {}


    private Student(StudentBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.grade = builder.grade;
        this.email = builder.email;
        this.phone = builder.phone;
        this.user = builder.user;
        if (builder.status != null) {
            this.status = builder.status;
        }
        if (builder.createdAt != null) {
            this.createdAt = builder.createdAt;
        }
    }

    public static class StudentBuilder {
        private Long id;
        private String name;
        private String grade;
        private String email;
        private String phone;
        private String status;
        private LocalDateTime createdAt;
        private User user;

        // Конструктор с обязательным параметром
        public StudentBuilder(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            this.name = name;
        }

        // Конструктор без параметров для обратной совместимости
        public StudentBuilder() {}

        public StudentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudentBuilder grade(String grade) {
            this.grade = grade;
            return this;
        }

        public StudentBuilder email(String email) {
            this.email = email;
            return this;
        }

        public StudentBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public StudentBuilder status(String status) {
            this.status = status;
            return this;
        }

        public StudentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudentBuilder user(User user) {
            this.user = user;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', grade='" + grade + "'}";
    }
}