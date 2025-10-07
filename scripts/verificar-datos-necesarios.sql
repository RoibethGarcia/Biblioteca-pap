-- Script para verificar que existan los datos necesarios para crear préstamos

-- 1. Verificar si existe un bibliotecario
SELECT 'BIBLIOTECARIOS:' as Info;
SELECT * FROM Bibliotecario;

-- 2. Si no existe ningún bibliotecario, crear uno
-- Descomentar las siguientes líneas si es necesario:
-- INSERT INTO Bibliotecario (id, nombre, apellido, email, password, numeroEmpleado, fechaIngreso) 
-- VALUES (1, 'Sistema', 'Automatico', 'sistema@biblioteca.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'EMP001', CURRENT_DATE);

-- 3. Verificar lectores
SELECT 'LECTORES:' as Info;
SELECT id, nombre, email FROM Lector;

-- 4. Verificar materiales (libros)
SELECT 'LIBROS DISPONIBLES:' as Info;
SELECT id, titulo, donante FROM Libro;

-- 5. Verificar préstamos existentes
SELECT 'PRESTAMOS EXISTENTES:' as Info;
SELECT p.id, l.nombre as lector, b.nombre as bibliotecario, p.estado 
FROM Prestamo p
LEFT JOIN Lector l ON p.lector_id = l.id
LEFT JOIN Bibliotecario b ON p.bibliotecario_id = b.id;

