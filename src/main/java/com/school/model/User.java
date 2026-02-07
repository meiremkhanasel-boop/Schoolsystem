package com.school.model;

public class User {
    private String login;
    private String password;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
    // Геттеры и сеттеры (Alt+Insert в IDEA)
    public String getLogin() { return login; }
    public String getPassword() { return password; }
}