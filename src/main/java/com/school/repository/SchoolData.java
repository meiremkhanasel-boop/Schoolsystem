package com.school.repository;

import com.school.model.Student;
import com.school.model.Teacher;
import com.school.model.User;
import java.util.ArrayList;
import java.util.List;

public class SchoolData {
    private static List<Student> students = new ArrayList<>();
    private static List<Teacher> teachers = new ArrayList<>();
    private static List<User> users = new ArrayList<>();

    static {
        // Тестовые данные
        students.add(new Student(1L, "Иван Иванов", "10-А"));
        teachers.add(new Teacher(1L, "Ольга Петровна", "Математика"));
        users.add(new User("admin", "123"));
    }

    public static List<Student> getStudents() { return students; }
    public static List<Teacher> getTeachers() { return teachers; }
    public static List<User> getUsers() { return users; }

    public static void addStudent(Student s) { students.add(s); }
    public static void addTeacher(Teacher t) { teachers.add(t); }
    public static void addUser(User u) { users.add(u); }
}