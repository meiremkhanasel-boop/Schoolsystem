-- SQL скрипт для добавления поля email к таблице users (SQL Server)
-- Выполните этот скрипт один раз

-- Для SQL Server
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'email')
BEGIN
    ALTER TABLE users ADD email VARCHAR(255) NULL UNIQUE;
    PRINT 'Поле email успешно добавлено в таблицу users';
END
ELSE
BEGIN
    PRINT 'Поле email уже существует в таблице users';
END;

