-- Script para verificar bibliotecarios existentes
SELECT b.id, u.nombre, u.email, b.numeroEmpleado 
FROM bibliotecarios b 
JOIN usuarios u ON b.id = u.id;

-- Si no hay ninguno, crear uno
INSERT INTO usuarios (nombre, email, password) 
SELECT 'Admin Biblioteca', 'admin@biblioteca.com', 'admin123'
WHERE NOT EXISTS (SELECT 1 FROM bibliotecarios);

-- Obtener el ID del último usuario insertado
SET @ultimo_id = LAST_INSERT_ID();

-- Crear bibliotecario si fue necesario
INSERT INTO bibliotecarios (id, numeroEmpleado) 
SELECT @ultimo_id, 'EMP001'
WHERE @ultimo_id > 0 AND NOT EXISTS (SELECT 1 FROM bibliotecarios WHERE id = @ultimo_id);

-- Verificar resultado
SELECT b.id, u.nombre, u.email, b.numeroEmpleado 
FROM bibliotecarios b 
JOIN usuarios u ON b.id = u.id;

