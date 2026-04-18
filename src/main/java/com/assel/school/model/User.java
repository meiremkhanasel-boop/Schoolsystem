package com.assel.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, message = "Логин должен содержать минимум 3 символа")
    @NotBlank(message = "Логин не может быть пустым")
    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    @Column(nullable = false, length = 255)
    private String password;

    @Email(message = "Некорректный формат email")
    @Column(nullable = true, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String role = "USER";

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLogin() { return username; }
    public void setLogin(String login) { this.username = login; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}