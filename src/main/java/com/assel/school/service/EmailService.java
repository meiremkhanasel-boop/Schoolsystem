package com.assel.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:meiremkhanasel@gmail.com}")
    private String senderEmail;

    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.isBlank()) {
            log.warn("❌ Попытка отправить письмо без адресата. Тема: {}", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("✉️ Email успешно отправлен на: {} - {}", to, subject);
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке email на адрес {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        sendEmail(to, subject, text);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✉️ HTML Email отправлен: {} - {}", to, subject);
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке HTML email [{}]: {}", to, e.getMessage(), e);
            throw new RuntimeException("HTML Email sending failed", e);
        }
    }

    public void sendRegistrationEmail(String to, String username) {
        String subject = "Добро пожаловать в SchoolSystem!";
        String htmlContent = buildRegistrationEmail(username);
        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendPasswordChangeEmail(String to, String username) {
        String subject = "Пароль успешно изменен";
        String htmlContent = buildPasswordChangeEmail(username);
        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendTeacherNotificationEmail(String to, String name, String subject, String subjectName) {
        String emailSubject = "Вы добавлены в качестве преподавателя";
        String htmlContent = buildTeacherNotificationEmail(name, subject, subjectName);
        sendHtmlEmail(to, emailSubject, htmlContent);
    }


    public void sendRatingNotificationEmail(String to, String studentName, String subjectName, String rating) {
        String subject = "Новая оценка в предмете " + subjectName;
        String htmlContent = buildRatingNotificationEmail(studentName, subjectName, rating);
        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendHomeworkNotificationEmail(String to, String studentName, String subjectName, String description) {
        String subject = "Новая домашка по предмету " + subjectName;
        String htmlContent = buildHomeworkNotificationEmail(studentName, subjectName, description);
        sendHtmlEmail(to, subject, htmlContent);
    }

    private String buildRegistrationEmail(String username) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>Добро пожаловать! 🎓</h2>" +
                "<p>Привет, <strong>" + username + "</strong>!</p>" +
                "<p>Спасибо за регистрацию в SchoolSystem. Ваш аккаунт успешно создан.</p>" +
                "<p>Теперь вы можете:</p>" +
                "<ul>" +
                "<li>Просматривать расписание</li>" +
                "<li>Отслеживать оценки</li>" +
                "<li>Взаимодействовать с преподавателями</li>" +
                "</ul>" +
                "<p style='margin-top: 30px; color: #7f8c8d;'>С уважением,<br>Команда SchoolSystem</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }


    private String buildPasswordChangeEmail(String username) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>Пароль изменен ✓</h2>" +
                "<p>Привет, <strong>" + username + "</strong>!</p>" +
                "<p>Ваш пароль был успешно изменен.</p>" +
                "<p style='color: #e74c3c;'>⚠️ Если это были не вы, пожалуйста, свяжитесь с администратором.</p>" +
                "<p style='margin-top: 30px; color: #7f8c8d;'>С уважением,<br>Команда SchoolSystem</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildTeacherNotificationEmail(String name, String subject, String subjectName) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>Вы назначены преподавателем 👨‍🏫</h2>" +
                "<p>Привет, <strong>" + name + "</strong>!</p>" +
                "<p>Вы добавлены в качестве преподавателя по предмету <strong>" + subjectName + "</strong>.</p>" +
                "<p>Вы можете начать добавлять студентов и оценки.</p>" +
                "<p style='margin-top: 30px; color: #7f8c8d;'>С уважением,<br>Администрация SchoolSystem</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }


    private String buildRatingNotificationEmail(String studentName, String subjectName, String rating) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>Новая оценка 📊</h2>" +
                "<p>Привет, <strong>" + studentName + "</strong>!</p>" +
                "<p>Вам поставлена оценка <strong style='color: #27ae60; font-size: 20px;'>" + rating + "</strong> " +
                "по предмету <strong>" + subjectName + "</strong>.</p>" +
                "<p>Просмотрите детали в разделе 'Оценки' вашего профиля.</p>" +
                "<p style='margin-top: 30px; color: #7f8c8d;'>С уважением,<br>Команда SchoolSystem</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildHomeworkNotificationEmail(String studentName, String subjectName, String description) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c3e50;'>Новая домашка 📝</h2>" +
                "<p>Привет, <strong>" + studentName + "</strong>!</p>" +
                "<p>Вам добавлена новая домашняя работа по предмету <strong>" + subjectName + "</strong>.</p>" +
                "<p>Задание:</p>" +
                "<blockquote style='border-left: 4px solid #3498db; padding-left: 10px; color: #555;'>" + description + "</blockquote>" +
                "<p style='margin-top: 30px; color: #7f8c8d;'>С уважением,<br>Команда SchoolSystem</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
