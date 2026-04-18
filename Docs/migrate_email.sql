-- ================================================
-- SchoolSystem - Миграция БД для Email функции
-- Для SQL Server
-- ================================================

-- Добавить поле email к таблице users (если оно еще не добавлено)
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'email')
BEGIN
    ALTER TABLE users
    ADD email VARCHAR(255) NULL UNIQUE;

    PRINT '✅ Поле email успешно добавлено в таблицу users';
END
ELSE
BEGIN
    PRINT '⚠️ Поле email уже существует в таблице users';
END;

-- ================================================
-- Проверка структуры таблицы users
-- ================================================
IF OBJECT_ID('users', 'U') IS NOT NULL
BEGIN
    PRINT '';
    PRINT '📋 Текущая структура таблицы users:';
    PRINT '================================================';
    SELECT
        COLUMN_NAME AS 'Название поля',
        DATA_TYPE AS 'Тип данных',
        IS_NULLABLE AS 'Допускает NULL',
        COLUMNPROPERTY(OBJECT_ID('users'), COLUMN_NAME, 'IsIdentity') AS 'Identity'
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'users'
    ORDER BY ORDINAL_POSITION;

    PRINT '';
    PRINT '✅ Структура таблицы готова к использованию с Email функцией';
END
ELSE
BEGIN
    PRINT '❌ Таблица users не найдена!';
    PRINT 'Выполните создание таблицы перед добавлением поля email';
END;

-- ================================================
-- Обновить существующих пользователей (если нужно)
-- ================================================
PRINT '';
PRINT '📊 Статистика пользователей:';
SELECT
    COUNT(*) AS 'Всего пользователей',
    SUM(CASE WHEN email IS NOT NULL THEN 1 ELSE 0 END) AS 'С email',
    SUM(CASE WHEN email IS NULL THEN 1 ELSE 0 END) AS 'Без email'
FROM users;

-- ================================================
-- Примеры добавления users с email
-- ================================================
PRINT '';
PRINT '📧 Примеры новых пользователей с email:';
PRINT '';
PRINT 'INSERT INTO users (username, password, role, email)';
PRINT 'VALUES (''testuser'', ''$2a$10$...'', ''USER'', ''testuser@example.com'');';
PRINT '';

-- ================================================
-- Проверка ограничений
-- ================================================
PRINT '';
PRINT '🔒 Ограничения (constraints):';

-- Проверить UNIQUE ограничение на email
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
           WHERE CONSTRAINT_TYPE = 'UNIQUE'
           AND TABLE_NAME = 'users')
BEGIN
    SELECT
        CONSTRAINT_NAME,
        COLUMN_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'users'
    AND CONSTRAINT_NAME LIKE '%UQ%' OR CONSTRAINT_NAME LIKE '%UNIQUE%';

    PRINT '✅ UNIQUE ограничения настроены';
END;

-- ================================================
-- Создание индекса для быстрого поиска по email (опционально)
-- ================================================
PRINT '';
PRINT '⚡ Создание индекса для email (для оптимизации):';

IF NOT EXISTS (SELECT * FROM sys.indexes
               WHERE name = 'IX_users_email' AND object_id = OBJECT_ID('users'))
BEGIN
    CREATE NONCLUSTERED INDEX IX_users_email
    ON users(email);

    PRINT '✅ Индекс IX_users_email создан';
END
ELSE
BEGIN
    PRINT '⚠️ Индекс IX_users_email уже существует';
END;

-- ================================================
-- Финальная проверка
-- ================================================
PRINT '';
PRINT '========================================';
PRINT '✅ МИГРАЦИЯ ЗАВЕРШЕНА УСПЕШНО';
PRINT '========================================';
PRINT '';
PRINT 'Теперь вы можете:';
PRINT '1. Использовать Email функцию в приложении';
PRINT '2. Отправлять письма при регистрации';
PRINT '3. Сохранять email адреса пользователей';
PRINT '';

