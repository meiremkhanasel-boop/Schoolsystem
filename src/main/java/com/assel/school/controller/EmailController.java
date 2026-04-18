package com.assel.school.controller;

import com.assel.school.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-simple")
    public ResponseEntity<?> sendSimpleEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String subject = request.get("subject");
            String text = request.get("text");

            if (to == null || subject == null || text == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Требуются поля: to, subject, text"));
            }

            emailService.sendSimpleEmail(to, subject, text);
            log.info("📧 Простое письмо отправлено: {}", to);

            return ResponseEntity.ok(Map.of("success", true, "message", "Письмо отправлено"));
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке письма: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Ошибка при отправке письма: " + e.getMessage()));
        }
    }

    @PostMapping("/send-html")
    public ResponseEntity<?> sendHtmlEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String subject = request.get("subject");
            String htmlContent = request.get("htmlContent");

            if (to == null || subject == null || htmlContent == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Требуются поля: to, subject, htmlContent"));
            }

            emailService.sendHtmlEmail(to, subject, htmlContent);
            log.info("📧 HTML письмо отправлено: {}", to);

            return ResponseEntity.ok(Map.of("success", true, "message", "HTML письмо отправлено"));
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке HTML письма: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Ошибка при отправке HTML письма: " + e.getMessage()));
        }
    }

    @PostMapping("/send-registration")
    public ResponseEntity<?> sendRegistrationEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String username = request.get("username");

            if (to == null || username == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Требуются поля: to, username"));
            }

            emailService.sendRegistrationEmail(to, username);
            log.info("📧 Письмо регистрации отправлено: {}", to);

            return ResponseEntity.ok(Map.of("success", true, "message", "Письмо регистрации отправлено"));
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке письма регистрации: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Ошибка: " + e.getMessage()));
        }
    }

    @PostMapping("/send-password-change")
    public ResponseEntity<?> sendPasswordChangeEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String username = request.get("username");

            if (to == null || username == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Требуются поля: to, username"));
            }

            emailService.sendPasswordChangeEmail(to, username);
            log.info("📧 Письмо смены пароля отправлено: {}", to);

            return ResponseEntity.ok(Map.of("success", true, "message", "Письмо смены пароля отправлено"));
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Ошибка: " + e.getMessage()));
        }
    }

    @PostMapping("/send-rating")
    public ResponseEntity<?> sendRatingEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String studentName = request.get("studentName");
            String subjectName = request.get("subjectName");
            String rating = request.get("rating");

            if (to == null || studentName == null || subjectName == null || rating == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Требуются все поля"));
            }

            emailService.sendRatingNotificationEmail(to, studentName, subjectName, rating);
            log.info("📧 Письмо об оценке отправлено: {}", to);

            return ResponseEntity.ok(Map.of("success", true, "message", "Письмо об оценке отправлено"));
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Ошибка: " + e.getMessage()));
        }
    }
}

