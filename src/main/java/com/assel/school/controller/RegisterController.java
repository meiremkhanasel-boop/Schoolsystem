package com.assel.school.controller;

import com.assel.school.service.CustomUserDetailsService;
import com.assel.school.service.EmailService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@Validated
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;

    public RegisterController(CustomUserDetailsService userDetailsService,
                            EmailService emailService) {
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam @NotBlank @Size(min = 3) String username,
            @RequestParam @NotBlank @Size(min = 6) String password,
            @RequestParam(required = false) @Email(message = "Некорректный формат email") String email,
            @RequestParam(required = false) String fullname,
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        if (username == null || username.isBlank() || username.length() < 3) {
            model.addAttribute("error", "Логин должен содержать минимум 3 символа");
            return "register";
        }
        if (password == null || password.length() < 6) {
            model.addAttribute("error", "Пароль должен содержать минимум 6 символов");
            return "register";
        }
        if (userDetailsService.existsByUsername(username)) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "register";
        }

        try {
            userDetailsService.register(username.trim(), password, email);
            log.info("✓ Пользователь зарегистрирован: {}", username);

            if (email != null && !email.isBlank()) {
                try {
                    emailService.sendRegistrationEmail(email, username);
                    log.info("✉️ Письмо регистрации отправлено на: {}", email);
                    redirectAttributes.addFlashAttribute("registeredMessage", "Вам отправлено письмо для подтверждения. Проверьте вашу почту.");
                } catch (Exception e) {
                    log.error("⚠️ Не удалось отправить письмо: {}", e.getMessage(), e);
                    redirectAttributes.addFlashAttribute("registrationWarning", "Регистрация прошла успешно, но возникла проблема с отправкой письма");
                }
            } else {
                 redirectAttributes.addFlashAttribute("registeredMessage", "Регистрация прошла успешно! Войдите.");
            }

            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            log.error("❌ Ошибка регистрации: {}", e.getMessage());
            return "register";
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleValidationException(ConstraintViolationException e, Model model) {
        model.addAttribute("error", "Ошибка валидации: " + e.getMessage());
        return "register";
    }
}