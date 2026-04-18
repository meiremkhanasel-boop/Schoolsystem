
USE SchoolDB;
GO

DECLARE @constraintName NVARCHAR(200);
SELECT @constraintName = d.name
FROM sys.default_constraints d
JOIN sys.columns c ON d.parent_object_id = c.object_id AND d.parent_column_id = c.column_id
JOIN sys.tables t ON c.object_id = t.object_id
WHERE t.name = 'users' AND c.name = 'role';

IF @constraintName IS NOT NULL
BEGIN
    EXEC('ALTER TABLE users DROP CONSTRAINT ' + @constraintName);
    PRINT 'DEFAULT constraint на role удалён: ' + @constraintName;
END

DECLARE @uc NVARCHAR(200);
SELECT @uc = d.name
FROM sys.default_constraints d
JOIN sys.columns c ON d.parent_object_id = c.object_id AND d.parent_column_id = c.column_id
JOIN sys.tables t ON c.object_id = t.object_id
WHERE t.name = 'users' AND c.name = 'username';

IF @uc IS NOT NULL
BEGIN
    EXEC('ALTER TABLE users DROP CONSTRAINT ' + @uc);
    PRINT 'DEFAULT constraint на username удалён.';
END

PRINT '✅ Готово! Теперь Hibernate не будет выдавать WARN при ALTER COLUMN.';
GO

