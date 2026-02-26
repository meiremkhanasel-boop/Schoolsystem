package com.school.controller;

import com.school.model.User;
import com.school.repository.SchoolData;
import com.school.util.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginServlet {

    private final JwtTokenProvider jwtTokenProvider;

    public LoginServlet() {
        this.jwtTokenProvider = new JwtTokenProvider();
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        boolean userExists = SchoolData.getUsers().stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()) &&
                             u.getPassword().equals(user.getPassword()));

        if (userExists) {
            String token = jwtTokenProvider.generateToken(user.getLogin());
            response.put("success", true);
            response.put("message", "Сәтті кіру");
            response.put("user", user.getLogin());
            response.put("token", token);
            response.put("tokenType", "Bearer");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Қате логин немесе пароль");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            response.put("success", false);
            response.put("message", "Логин бос болуы мүмкін емес");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("success", false);
            response.put("message", "Пароль бос болуы мүмкін емес");
            return ResponseEntity.badRequest().body(response);
        }

        boolean exists = SchoolData.getUsers().stream()
                .anyMatch(u -> u.getLogin().equals(user.getLogin()));

        if (exists) {
            response.put("success", false);
            response.put("message", "Бұл логин әлде тіркеулі");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        SchoolData.getUsers().add(user);
        String token = jwtTokenProvider.generateToken(user.getLogin());
        response.put("success", true);
        response.put("message", "Сәтті тіркеу");
        response.put("user", user.getLogin());
        response.put("token", token);
        response.put("tokenType", "Bearer");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}