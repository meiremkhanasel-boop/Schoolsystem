package com.assel.school.service;

import com.assel.school.model.User;
import com.assel.school.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("🔹 CustomUserDetailsService initialized");
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        log.debug("🔍 Loading user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                        log.warn("❌ User '{}' not found", username);
                        return new UsernameNotFoundException("Пайдаланушы табылмады: " + username);
                });

        log.info("✅ Пользователь найден: {}, пароль в БД: {} (length={})",
                username,
                user.getPassword() != null ? (user.getPassword().startsWith("$2a$") ? "BCrypt хеш ✓" : "ТЕКСТ ❌") : "null",
                user.getPassword() != null ? user.getPassword().length() : 0);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .build();
    }


    public User register(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Бұл логин бос емес: " + username);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User register(String username, String rawPassword, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Бұл логин бос емес: " + username);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User registerWithRole(String username, String rawPassword, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Бұл логин бос емес: " + username);
        }
        User user = new User();
        user.setUsername(username);

        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("🔐 Пароль кодирован для пользователя '{}': rawPassword length={}, encodedPassword length={}",
                username, rawPassword.length(), encodedPassword.length());
        log.debug("🔐 Закодированный пароль: {}", encodedPassword);

        user.setPassword(encodedPassword);
        user.setRole(role);

        User savedUser = userRepository.save(user);
        log.info("✅ Пользователь '{}' сохранен в БД с ролью '{}'", username, role);

        return savedUser;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean resetPassword(String username, String newRawPassword) {
        return userRepository.findByUsername(username).map(user -> {
            user.setPassword(passwordEncoder.encode(newRawPassword));
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        return userRepository.findByUsername(username).map(user -> {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return false;
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
}
