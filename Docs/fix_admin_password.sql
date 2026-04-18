

USE SchoolDB;
GO

SELECT id, username,
       LEFT(password, 15) AS password_preview,
       CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%'
            THEN 'BCrypt OK' ELSE '!!! НЕ ЗАШИФРОВАН !!!' END AS status,
       role
FROM users;
GO


DELETE FROM users WHERE username IN ('admin', 'user');
GO

INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$slYQmyNdgTY18LMwnsBzb.lqMVIPwWh4uZJSCuqI/2VZM9PkDIoYi', 'ADMIN'),
('user',  '$2a$10$ByIUiNaRfBBSJtXNlBpxQ.W0HIfv4RyN7x6VzJRFCRkfNQ0lq4RJq', 'USER');
GO

SELECT id, username,
       CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%'
            THEN 'BCrypt OK' ELSE 'ОШИБКА' END AS password_status,
       role
FROM users;
GO

PRINT '=========================================';
PRINT 'Готово! Используйте для входа:';
PRINT '  Логин: admin   Пароль: admin123';
PRINT '  Логин: user    Пароль: user123';
PRINT '=========================================';
PRINT 'ВАЖНО: Перезапустите Spring Boot приложение!';
GO
