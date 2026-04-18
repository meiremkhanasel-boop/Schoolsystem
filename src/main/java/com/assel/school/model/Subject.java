package com.assel.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название предмета обязательно")
    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Subject() {}


    private Subject(SubjectBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        if (builder.createdAt != null) {
            this.createdAt = builder.createdAt;
        }
    }

    public static class SubjectBuilder {
        private Long id;
        private String name;
        private String description;
        private LocalDateTime createdAt;

        public SubjectBuilder(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            this.name = name;
        }

        public SubjectBuilder() {}

        public SubjectBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SubjectBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubjectBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SubjectBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Subject build() {
            return new Subject(this);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

