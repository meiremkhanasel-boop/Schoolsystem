package com.school.model;

public class Student {
    private Long id;
    private String name;
    private String grade; // Например, "10-А"

    // Конструктор без параметров (нужен для некоторых фреймворков)
    public Student() {}

    // Конструктор для быстрого создания объекта
    public Student(Long id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    // Геттеры и Сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}