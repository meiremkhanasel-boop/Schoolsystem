package com.assel.school.controller;

import com.assel.school.model.User;
import com.assel.school.repository.UserRepository;
import com.assel.school.service.CustomUserDetailsService;
import com.assel.school.service.StudentService;
import com.assel.school.service.TeacherService;
import com.assel.school.service.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final CustomUserDetailsService userService;
    private final UserRepository userRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final EmailService emailService;
    private final DataSource dataSource;

    public AdminController(CustomUserDetailsService userService,
                           UserRepository userRepository,
                           StudentService studentService,
                           TeacherService teacherService,
                           EmailService emailService,
                           DataSource dataSource) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.emailService = emailService;
        this.dataSource = dataSource;
    }

    @GetMapping("")
    public String adminPanel(Model model, Authentication auth) {
        try {
            model.addAttribute("username", auth != null ? auth.getName() : "Admin");
            model.addAttribute("isAdmin", true);
            System.out.println("🔍 AdminPanel called successfully");
        } catch (Exception e) {
            log.error("Error in adminPanel", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке панели администратора");
        }
        return "admin";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam(defaultValue = "USER") String role,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.registerWithRole(username.trim(), password, role);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Пользователь '" + username + "' создан с ролью " + role);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ " + e.getMessage());
        } catch (Exception e) {
            log.error("Error adding user", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка системы: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/change-role/{id}")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String newRole,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            userRepository.findById(id).ifPresent(user -> {
                if (!user.getUsername().equals(auth.getName())) {
                    user.setRole(newRole);
                    userRepository.save(user);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "✅ Роль пользователя изменена на " + newRole);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "❌ Нельзя изменить свою роль!");
                }
            });
        } catch (Exception e) {
            log.error("Error changing role", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка при смене роли");
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            userRepository.findById(id).ifPresent(user -> {
                if (!user.getUsername().equals(auth.getName())) {
                    userRepository.delete(user);
                    redirectAttributes.addFlashAttribute("successMessage", "🗑 Пользователь удалён.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "❌ Нельзя удалить самого себя!");
                }
            });
        } catch (Exception e) {
            log.error("Error deleting user", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка при удалении пользователя");
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/reset-password/{id}")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            userRepository.findById(id).ifPresent(user -> {
                userService.resetPassword(user.getUsername(), newPassword);
                redirectAttributes.addFlashAttribute("successMessage", "🔑 Пароль успешно сброшен.");
            });
        } catch (Exception e) {
            log.error("Error resetting password", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка сброса пароля");
        }
        return "redirect:/admin";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        try {
            List<User> users = userService.findAll();
            model.addAttribute("users", users != null ? users : new ArrayList<>());
        } catch (Exception e) {
            log.error("Error getting users", e);
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("errorMessage", "Ошибка при загрузке пользователей");
        }
        return "admin/users";
    }

    @GetMapping("/create")
    public String createUserForm(Model model){
        try {
            model.addAttribute("user", new User());
        } catch (Exception e) {
            log.error("Error in createUserForm", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке формы создания пользователя");
        }
        return "admin/create-user";
    }

    @PostMapping("/create")
    public String createUser(User user, RedirectAttributes redirectAttributes){
        try {
            log.info("🔍 Creating user: username={}, password={}, role={}",
                    user.getUsername(),
                    user.getPassword() != null ? "***" : "null",
                    user.getRole());

            userService.registerWithRole(user.getUsername(), user.getPassword(), user.getRole());

            log.info("✅ Пользователь создан: {}", user.getUsername());

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                try {
                    String subject = "Ваш аккаунт создан в SchoolSystem";
                    String htmlContent = buildAdminCreatedUserEmail(user.getUsername(), user.getRole());
                    emailService.sendHtmlEmail(user.getEmail(), subject, htmlContent);
                    log.info("✉️ Email отправлен новому пользователю: {}", user.getEmail());
                } catch (Exception e) {
                    log.warn("⚠️ Не удалось отправить email: {}", e.getMessage());
                }
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Пользователь '" + user.getUsername() + "' успешно создан");
        } catch (Exception e) {
            log.error("❌ Ошибка создания пользователя: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    private String buildAdminCreatedUserEmail(String username, String role) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>🎓 Добро пожаловать в SchoolSystem!</h2>" +
                "<p>Привет, <strong>" + username + "</strong>!</p>" +
                "<p>Ваш аккаунт был создан администратором.</p>" +
                "<p><strong>Роль:</strong> " + role + "</p>" +
                "<p style='background-color: #ecf0f1; padding: 10px; border-radius: 5px;'>" +
                "<strong>⚠️ Важно:</strong> Смените пароль при первом входе в систему для безопасности." +
                "</p>" +
                "<p><a href='http://localhost:8080/login' style='background-color: #1cc88a; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Перейти к входу</a></p>" +
                "<p style='margin-top: 30px; color: #7f8c8d;'>С уважением,<br>Администрация SchoolSystem</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model){
        try {
            User user = userRepository.findById(id).orElse(null);
            model.addAttribute("user", user);
        } catch (Exception e) {
            log.error("Error in editUser", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке пользователя");
        }
        return "admin/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes){
        try {
            userRepository.findById(id).ifPresent(existingUser -> {
                try {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setRole(user.getRole());
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        userService.resetPassword(existingUser.getUsername(), user.getPassword());
                    } else {
                        userRepository.save(existingUser);
                    }
                    redirectAttributes.addFlashAttribute("successMessage", "✅ Пользователь успешно обновлен!");
                } catch (Exception e) {
                    log.error("Error updating user", e);
                    redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка при обновлении пользователя");
                }
            });
        } catch (Exception e) {
            log.error("Error in updateUser", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка при обновлении пользователя");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUserLab(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "✅ Пользователь удалён.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Пользователь не найден.");
            }
        } catch (Exception e) {
            log.error("Error deleting user", e);
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Ошибка при удалении пользователя");
        }
        return "redirect:/admin/users";
    }
}
