package com.school.model;

public class Teacher {
    private Long id;
    private String name;
    private String subject; // Например, "Математика"

    public Teacher() {}

    public Teacher(Long id, String name, String subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
    }

    // Геттеры и Сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}