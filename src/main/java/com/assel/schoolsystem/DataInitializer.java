package com.assel.schoolsystem;

import com.assel.school.model.Student;
import com.assel.school.model.Teacher;
import com.assel.school.model.Subject;
import com.assel.school.repository.StudentRepository;
import com.assel.school.repository.TeacherRepository;
import com.assel.school.repository.UserRepository;
import com.assel.school.repository.SubjectRepository;
import com.assel.school.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    public DataInitializer(CustomUserDetailsService userDetailsService,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           StudentRepository studentRepository,
                           TeacherRepository teacherRepository,
                           SubjectRepository subjectRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository    = userRepository;
        this.passwordEncoder   = passwordEncoder;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public void run(String... args) {
        log.info("🚀 DataInitializer starting...");

        ensureUser("admin", "admin123", "ADMIN");
        ensureUser("user",  "user123",  "USER");

        if (subjectRepository.count() == 0) {
           subjectRepository.save(new Subject.SubjectBuilder().name("Математика").description("Алгебра и Геометрия").build());
           subjectRepository.save(new Subject.SubjectBuilder().name("Физика").description("Механика и Термодинамика").build());
           subjectRepository.save(new Subject.SubjectBuilder().name("Химия").description("Органическая и Неорганическая").build());
           subjectRepository.save(new Subject.SubjectBuilder().name("Информатика").description("Программирование и ИКТ").build());
           subjectRepository.save(new Subject.SubjectBuilder().name("История").description("Всемирная история и История Казахстана").build());
           subjectRepository.save(new Subject.SubjectBuilder().name("Английский язык").description("Foreign Language").build());
           subjectRepository.save(new Subject.SubjectBuilder().name("Биология").description("Анатомия и Ботаника").build());
           log.info("✅ {} предметов добавлено", subjectRepository.count());
        }

        if (studentRepository.count() == 0) {
            Student s1 = new Student.StudentBuilder().name("Мейремхан Асель").grade("10-А").email("asel@school.kz").phone("+7 700 111 2233").status("active").build();
            Student s2 = new Student.StudentBuilder().name("Жансая Абдикова").grade("10-А").email("zhansaya@school.kz").phone("+7 700 222 3344").status("active").build();
            Student s3 = new Student.StudentBuilder().name("Нурлан Сейткали").grade("11-Б").email("nurlan@school.kz").phone("+7 700 333 4455").status("active").build();
            Student s4 = new Student.StudentBuilder().name("Дина Ахметова").grade("11-А").email("dina@school.kz").phone("+7 700 444 5566").status("active").build();
            Student s5 = new Student.StudentBuilder().name("Арман Жаксыбеков").grade("9-В").email("arman@school.kz").phone("+7 700 555 6677").status("inactive").build();
            Student s6 = new Student.StudentBuilder().name("Айдана Сулейменова").grade("9-В").email("aidana@school.kz").status("active").build();
            Student s7 = new Student.StudentBuilder().name("Бекзат Ержанов").grade("10-Б").email("bekzat@school.kz").status("active").build();

            studentRepository.save(s1); studentRepository.save(s2); studentRepository.save(s3);
            studentRepository.save(s4); studentRepository.save(s5); studentRepository.save(s6);
            studentRepository.save(s7);
            log.info("✅ {} студентов добавлено", studentRepository.count());
        }

        if (teacherRepository.count() == 0) {
            Teacher t1 = new Teacher("Шотха Мейремхан", "Математика");
            t1.setEmail("math@school.kz"); t1.setPhone("+7 701 100 2200"); t1.setExperience(12); t1.setStatus("active");
            Teacher t2 = new Teacher("Айгерим Токова", "Физика");
            t2.setEmail("phys@school.kz"); t2.setPhone("+7 701 200 3300"); t2.setExperience(8);  t2.setStatus("active");
            Teacher t3 = new Teacher("Данияр Усенов", "История");
            t3.setEmail("hist@school.kz"); t3.setExperience(15); t3.setStatus("active");
            Teacher t4 = new Teacher("Гульнар Бекова", "Казахский язык");
            t4.setEmail("kaz@school.kz");  t4.setExperience(20); t4.setStatus("active");
            Teacher t5 = new Teacher("Асель Нурова", "Информатика");
            t5.setEmail("it@school.kz");   t5.setPhone("+7 701 500 6600"); t5.setExperience(5); t5.setStatus("active");
            Teacher t6 = new Teacher("Серик Джаксыбеков", "Математика");
            t6.setEmail("math2@school.kz"); t6.setExperience(3); t6.setStatus("inactive");

            teacherRepository.save(t1); teacherRepository.save(t2); teacherRepository.save(t3);
            teacherRepository.save(t4); teacherRepository.save(t5); teacherRepository.save(t6);
            log.info("✅ {} учителей добавлено", teacherRepository.count());
        }
    }


    private void ensureUser(String username, String rawPassword, String role) {
        if (!userRepository.existsByUsername(username)) {
            com.assel.school.model.User u = new com.assel.school.model.User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(rawPassword));
            u.setRole(role);
            userRepository.save(u);
            log.info("✅ Создан пользователь '{}' / '{}' ({})", username, rawPassword, role);
        } else {
            userRepository.findByUsername(username).ifPresent(u -> {
                String stored = u.getPassword() == null ? "" : u.getPassword();
                boolean matches = passwordEncoder.matches(rawPassword, stored);

                if (!matches) {
                    // Force update password if it doesn't match
                    String newHash = passwordEncoder.encode(rawPassword);
                    u.setPassword(newHash);
                    u.setRole(role); // Ensure role is correct too
                    userRepository.save(u);
                    log.warn("🔄 Пароль '{}' ИСПРАВЛЕН на '{}' (Role: {}). Новый хэш: {}", username, rawPassword, role, newHash);
                } else {
                    log.info("✅ Пользователь '{}' — пароль совпадает. Role: {}", username, u.getRole());
                }
            });
        }
    }
}
