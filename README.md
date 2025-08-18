# 📚 Biblioteca PAP - Sistema de Gestión

Sistema de gestión de biblioteca desarrollado en Java con Hibernate y Swing para la interfaz de usuario.

## 🚀 Características

- **Gestión de Usuarios**: Lectores y Bibliotecarios
- **Gestión de Materiales**: Libros y Artículos Especiales
- **Sistema de Donaciones**: Registro de materiales donados
- **Gestión de Préstamos**: Control de préstamos de materiales
- **Base de Datos**: H2 (desarrollo) y MySQL (producción)

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Hibernate 6.x** (ORM)
- **H2 Database** (desarrollo)
- **MySQL** (producción)
- **Maven** (gestión de dependencias)
- **Swing** (interfaz gráfica)

## 📋 Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6+ (opcional, IntelliJ lo maneja automáticamente)
- Git

## 🔧 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/RoibethGarcia/biblioteca-pap.git
cd biblioteca-pap
```

### 2. Abrir en IntelliJ IDEA
1. **File** → **Open** → Seleccionar carpeta `biblioteca-pap`
2. **Import Maven project** (automático)
3. **Configure JDK 17** si no está configurado

### 3. Configurar Base de Datos

#### Opción A: H2 (Desarrollo - Recomendado)
- ✅ **Configurado por defecto**
- Los datos se guardan en `./target/h2db/biblioteca_pap`
- No requiere configuración adicional

#### Opción B: MySQL (Producción)
1. Crear base de datos:
```sql
CREATE DATABASE biblioteca_pap CHARACTER SET utf8mb4;
```

2. Configurar conexión:
   - Editar `src/main/resources/hibernate-mysql.cfg.xml`
   - Ajustar usuario y contraseña

3. Ejecutar con MySQL:
```bash
java -Ddb=mysql -jar target/biblioteca-pap.jar
```

## 🎯 Ejecutar la Aplicación

### Desde IntelliJ IDEA
1. Abrir `src/main/java/edu/udelar/pap/ui/Main.java`
2. Presionar **Shift + F10** (Run)

### Desde Línea de Comandos
```bash
# Con Maven
mvn -q -DskipTests exec:java

# Con Java directo (después de compilar)
java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.Main
```

## 📊 Verificar Datos en la Base de Datos

### Consola H2 Web
1. Ejecutar: `java -cp "target/classes:target/dependency/*" org.h2.tools.Console`
2. Abrir navegador en: `http://localhost:8082`
3. Configuración:
   - **JDBC URL**: `jdbc:h2:./target/h2db/biblioteca_pap`
   - **Usuario**: `sa`
   - **Contraseña**: (dejar vacío)

### Consultas SQL Ejemplo
```sql
-- Ver todos los lectores
SELECT * FROM LECTOR;

-- Ver todos los libros
SELECT * FROM LIBRO;

-- Ver préstamos activos
SELECT * FROM PRESTAMO WHERE ESTADO = 'ACTIVO';
```

## 🏗️ Estructura del Proyecto

```
biblioteca-pap/
├── src/main/java/edu/udelar/pap/
│   ├── domain/          # Entidades del dominio
│   ├── persistence/     # Configuración Hibernate
│   └── ui/             # Interfaz de usuario
├── src/main/resources/
│   ├── hibernate-h2.cfg.xml    # Config H2
│   └── hibernate-mysql.cfg.xml # Config MySQL
├── pom.xml             # Dependencias Maven
└── README.md          # Este archivo
```

## 🔄 Control de Versiones

- **Git** configurado con repositorio remoto
- **Branch principal**: `main`
- **Repositorio**: https://github.com/RoibethGarcia/biblioteca-pap.git

### Comandos Git útiles
```bash
# Ver estado
git status

# Ver historial
git log --oneline

# Crear nueva rama
git checkout -b nueva-funcionalidad

# Subir cambios
git add .
git commit -m "Descripción del cambio"
git push origin main
```

## 🐛 Solución de Problemas

### Error de conexión a base de datos
- Verificar que Java 17 esté instalado
- Limpiar y recompilar: `mvn clean compile`

### Error de dependencias
- Actualizar Maven: `mvn clean install`
- Refrescar proyecto en IntelliJ: **Maven** → **Reload All Maven Projects**

### Base de datos corrupta
- Eliminar carpeta `target/h2db/`
- Recompilar: `mvn clean compile`

## 📝 Licencia

Este proyecto es parte del curso PAP (Programación Avanzada y Persistencia).

## 👨‍💻 Autor

Roibeth Garcia - [GitHub](https://github.com/RoibethGarcia)

---

**Nota**: Este proyecto utiliza H2 como base de datos por defecto para facilitar el desarrollo. Para producción, se recomienda configurar MySQL.
