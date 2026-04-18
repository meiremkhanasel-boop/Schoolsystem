-- ============================================================
--  SchoolSystem — SQL Server Setup + Migration Script
--  Запустите этот скрипт в SQL Server Management Studio (SSMS)
-- ============================================================

-- 1. Создаём базу данных
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'SchoolDB')
BEGIN
    CREATE DATABASE SchoolDB;
    PRINT 'База данных SchoolDB создана успешно!';
END
ELSE
    PRINT 'База данных SchoolDB уже существует.';
GO

USE SchoolDB;
GO

-- 2. Создаём таблицу пользователей
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U')
BEGIN
    CREATE TABLE users (
        id       BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(100) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        role     NVARCHAR(50)  NOT NULL DEFAULT 'USER'
    );
    PRINT 'Таблица users создана!';
END
GO

-- 3. Создаём таблицу студентов
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='students' AND xtype='U')
BEGIN
    CREATE TABLE students (
        id         BIGINT IDENTITY(1,1) PRIMARY KEY,
        name       NVARCHAR(100) NOT NULL,
        grade      NVARCHAR(20)  NOT NULL,
        email      NVARCHAR(150) NULL,
        phone      NVARCHAR(50)  NULL,
        status     NVARCHAR(20)  NULL DEFAULT 'active',
        created_at DATETIME2     NOT NULL DEFAULT GETDATE()
    );
    PRINT 'Таблица students создана!';
END
ELSE
BEGIN
    -- Добавляем новые колонки если не существуют
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('students') AND name = 'email')
        ALTER TABLE students ADD email NVARCHAR(150) NULL;
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('students') AND name = 'phone')
        ALTER TABLE students ADD phone NVARCHAR(50) NULL;
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('students') AND name = 'status')
        ALTER TABLE students ADD status NVARCHAR(20) NULL DEFAULT 'active';
    -- Обновляем существующие записи
    UPDATE students SET status = 'active' WHERE status IS NULL;
    PRINT 'Таблица students обновлена (добавлены email, phone, status)!';
END
GO

-- 4. Создаём таблицу учителей
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='teachers' AND xtype='U')
BEGIN
    CREATE TABLE teachers (
        id         BIGINT IDENTITY(1,1) PRIMARY KEY,
        name       NVARCHAR(100) NOT NULL,
        subject    NVARCHAR(100) NOT NULL,
        email      NVARCHAR(150) NULL,
        phone      NVARCHAR(50)  NULL,
        experience INT           NULL,
        status     NVARCHAR(20)  NULL DEFAULT 'active',
        created_at DATETIME2     NOT NULL DEFAULT GETDATE()
    );
    PRINT 'Таблица teachers создана!';
END
ELSE
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('teachers') AND name = 'email')
        ALTER TABLE teachers ADD email NVARCHAR(150) NULL;
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('teachers') AND name = 'phone')
        ALTER TABLE teachers ADD phone NVARCHAR(50) NULL;
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('teachers') AND name = 'experience')
        ALTER TABLE teachers ADD experience INT NULL;
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('teachers') AND name = 'status')
        ALTER TABLE teachers ADD status NVARCHAR(20) NULL DEFAULT 'active';
    UPDATE teachers SET status = 'active' WHERE status IS NULL;
    PRINT 'Таблица teachers обновлена!';
END
GO

-- 5. Тестовые пользователи (пароль: admin123, user123 — BCrypt)
IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, password, role) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
    ('user',  '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1ohk7oT3K', 'USER');
    PRINT 'Пользователи admin/admin123 и user/user123 добавлены!';
END
GO

-- 6. Тестовые студенты
IF NOT EXISTS (SELECT * FROM students)
BEGIN
    INSERT INTO students (name, grade, email, phone, status) VALUES
    (N'Мейремхан Асель',   N'10-А', 'asel@school.kz',     '+7 700 111 2233', 'active'),
    (N'Жансая Абдикова',   N'10-А', 'zhansaya@school.kz', '+7 700 222 3344', 'active'),
    (N'Нурлан Сейткали',   N'11-Б', 'nurlan@school.kz',   '+7 700 333 4455', 'active'),
    (N'Дина Ахметова',     N'11-А', 'dina@school.kz',     '+7 700 444 5566', 'active'),
    (N'Арман Жаксыбеков',  N'9-В',  'arman@school.kz',    '+7 700 555 6677', 'inactive'),
    (N'Айдана Сулейменова',N'9-В',  'aidana@school.kz',   NULL,              'active'),
    (N'Бекзат Ержанов',    N'10-Б', 'bekzat@school.kz',   NULL,              'active');
    PRINT '7 студентов добавлены!';
END
GO

-- 7. Тестовые учителя
IF NOT EXISTS (SELECT * FROM teachers)
BEGIN
    INSERT INTO teachers (name, subject, email, phone, experience, status) VALUES
    (N'Шотха Мейремхан',   N'Математика',      'math@school.kz',  '+7 701 100 2200', 12, 'active'),
    (N'Айгерим Токова',    N'Физика',           'phys@school.kz',  '+7 701 200 3300',  8, 'active'),
    (N'Данияр Усенов',     N'История',          'hist@school.kz',  NULL,              15, 'active'),
    (N'Гульнар Бекова',    N'Казахский язык',   'kaz@school.kz',   NULL,              20, 'active'),
    (N'Асель Нурова',      N'Информатика',      'it@school.kz',    '+7 701 500 6600',  5, 'active'),
    (N'Серик Джаксыбеков', N'Математика',       'math2@school.kz', NULL,               3, 'inactive');
    PRINT '6 учителей добавлены!';
END
GO

-- 8. Итоговая проверка
SELECT 'users'    AS [Таблица], COUNT(*) AS [Записей] FROM users
UNION ALL
SELECT 'students', COUNT(*) FROM students
UNION ALL
SELECT 'teachers', COUNT(*) FROM teachers;
GO

PRINT '=== SchoolDB готова! Запустите приложение на http://localhost:8080 ===';
GO
--  Запустите этот скрипт в SQL Server Management Studio (SSMS)
-- ============================================================

-- 1. Создаём базу данных
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'SchoolDB')
BEGIN
    CREATE DATABASE SchoolDB;
    PRINT 'База данных SchoolDB создана успешно!';
END
ELSE
    PRINT 'База данных SchoolDB уже существует.';
GO

-- 2. Переключаемся на SchoolDB
USE SchoolDB;
GO

-- 3. Создаём таблицу пользователей
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U')
BEGIN
    CREATE TABLE users (
        id       BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(100) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        role     NVARCHAR(50)  NOT NULL DEFAULT 'USER'
    );
    PRINT 'Таблица users создана!';
END
GO

-- 4. Создаём таблицу студентов
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='students' AND xtype='U')
BEGIN
    CREATE TABLE students (
        id         BIGINT IDENTITY(1,1) PRIMARY KEY,
        name       NVARCHAR(100) NOT NULL,
        grade      NVARCHAR(20)  NOT NULL,
        created_at DATETIME2     NOT NULL DEFAULT GETDATE()
    );
    PRINT 'Таблица students создана!';
END
GO

-- 5. Создаём таблицу учителей
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='teachers' AND xtype='U')
BEGIN
    CREATE TABLE teachers (
        id         BIGINT IDENTITY(1,1) PRIMARY KEY,
        name       NVARCHAR(100) NOT NULL,
        subject    NVARCHAR(100) NOT NULL,
        created_at DATETIME2     NOT NULL DEFAULT GETDATE()
    );
    PRINT 'Таблица teachers создана!';
END
GO

-- 6. Вставляем тестовые данные (пароль: admin123, user123 — BCrypt)
IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, password, role) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
    ('user',  '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1ohk7oT3K', 'USER');
    PRINT 'Пользователи admin/admin123 и user/user123 добавлены!';
END
GO

-- 7. Тестовые студенты
IF NOT EXISTS (SELECT * FROM students)
BEGIN
    INSERT INTO students (name, grade) VALUES
    (N'Мейремхан Асель',   N'10-А'),
    (N'Жансая Абдикова',   N'10-А'),
    (N'Нурлан Сейткали',   N'11-Б'),
    (N'Дина Ахметова',     N'11-А'),
    (N'Арман Жаксыбеков',  N'9-В');
    PRINT '5 студентов добавлены!';
END
GO

-- 8. Тестовые учителя
IF NOT EXISTS (SELECT * FROM teachers)
BEGIN
    INSERT INTO teachers (name, subject) VALUES
    (N'Шотха Мейремхан',  N'Математика'),
    (N'Айгерим Токова',   N'Физика'),
    (N'Данияр Усенов',    N'История'),
    (N'Гульнар Бекова',   N'Казахский язык'),
    (N'Асель Нурова',     N'Информатика');
    PRINT '5 учителей добавлены!';
END
GO

-- 9. Проверка
SELECT 'users'    AS [Таблица], COUNT(*) AS [Записей] FROM users
UNION ALL
SELECT 'students' AS [Таблица], COUNT(*) AS [Записей] FROM students
UNION ALL
SELECT 'teachers' AS [Таблица], COUNT(*) AS [Записей] FROM teachers;
GO

PRINT '=== SchoolDB готова к использованию! ===';

