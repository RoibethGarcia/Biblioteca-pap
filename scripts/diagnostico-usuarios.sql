-- Script de diagnóstico para verificar usuarios registrados
-- y el estado de sus contraseñas

USE BD_Pap;

-- Ver todos los usuarios (tabla base)
SELECT 
    'USUARIOS' as tabla,
    id, 
    nombre, 
    email, 
    CASE 
        WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' THEN 'BCrypt Hash (OK)'
        WHEN LENGTH(password) < 20 THEN 'Texto Plano (ERROR)'
        ELSE CONCAT('Desconocido (', LENGTH(password), ' caracteres)')
    END as estado_password,
    LENGTH(password) as longitud_password,
    LEFT(password, 20) as inicio_password
FROM usuarios
ORDER BY id DESC;

-- Ver lectores
SELECT 
    'LECTORES' as tabla,
    u.id, 
    u.nombre, 
    u.email,
    l.zona,
    l.estado,
    l.fechaRegistro,
    CASE 
        WHEN u.password LIKE '$2a$%' OR u.password LIKE '$2b$%' THEN 'BCrypt Hash (OK)'
        WHEN LENGTH(u.password) < 20 THEN 'Texto Plano (ERROR)'
        ELSE CONCAT('Desconocido (', LENGTH(u.password), ' caracteres)')
    END as estado_password
FROM usuarios u
INNER JOIN lectores l ON u.id = l.id
ORDER BY u.id DESC;

-- Ver bibliotecarios
SELECT 
    'BIBLIOTECARIOS' as tabla,
    u.id, 
    u.nombre, 
    u.email,
    b.numeroEmpleado,
    CASE 
        WHEN u.password LIKE '$2a$%' OR u.password LIKE '$2b$%' THEN 'BCrypt Hash (OK)'
        WHEN LENGTH(u.password) < 20 THEN 'Texto Plano (ERROR)'
        ELSE CONCAT('Desconocido (', LENGTH(u.password), ' caracteres)')
    END as estado_password
FROM usuarios u
INNER JOIN bibliotecarios b ON u.id = b.id
ORDER BY u.id DESC;

