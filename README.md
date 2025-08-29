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
├── �� src/main/java/edu/udelar/pap/
│   ├── 🎮 controller/          # Controladores MVC
│   │   ├── BibliotecarioController.java
│   │   ├── DonacionController.java
│   │   ├── LectorController.java
│   │   ├── MainController.java
│   │   └── PrestamoController.java
│   │
│   ├── 🏗️ domain/              # Modelos de dominio
│   │   ├── ArticuloEspecial.java
│   │   ├── Bibliotecario.java
│   │   ├── DonacionMaterial.java
│   │   ├── EstadoLector.java
│   │   ├── EstadoPrestamo.java
│   │   ├── Lector.java
│   │   ├── Libro.java
│   │   ├── Prestamo.java
│   │   ├── Usuario.java
│   │   └── Zona.java
│   │
│   ├── 💾 persistence/         # Capa de persistencia
│   │   └── HibernateUtil.java
│   │
│   ├── ⚙️ service/             # Lógica de negocio
│   │   ├── BibliotecarioService.java
│   │   ├── DonacionService.java
│   │   ├── LectorService.java
│   │   └── PrestamoService.java
│   │
│   └── 🖥️ ui/                  # Interfaz de usuario
│       ├── ConfigChecker.java
│       ├── DatabaseTester.java
│       ├── DatabaseUtil.java
│       ├── DataViewer.java
│       ├── DateTextField.java
│       ├── InterfaceUtil.java
│       ├── MainRefactored.java  # Punto de entrada
│       ├── MaterialComboBoxItem.java
│       ├── SchemaGenerator.java
│       └── ValidacionesUtil.java
│
├── �� src/main/resources/      # Configuración
│   ├── hibernate-h2.cfg.xml
│   ├── hibernate-mysql-team.cfg.xml
│   └── hibernate-mysql.cfg.xml
│
├── 📁 target/                  # Archivos compilados
├── 📄 pom.xml                  # Configuración Maven
├── 📄 README.md
└── 🐚 *.sh                     # Scripts de ejecución
```

## 🔄 Control de Versiones

- **Git** configurado con repositorio remoto
- **Branch principal**: `main`
- **Repositorio**: https://github.com/RoibethGarcia/biblioteca-pap.git

### Comandos Git útiles
```bash
# 1️⃣ Ver el estado de tu repo
git status

# 2️⃣ Ver el historial resumido de commits
git log --oneline

# 3️⃣ Actualizar tu rama principal (main) antes de crear nueva branch
git checkout main
git pull origin main

# 4️⃣ Crear una nueva branch para tu funcionalidad o cambio
git checkout -b nueva-funcionalidad
# 👈 Trabajás en esta branch sin tocar main

# 5️⃣ Hacer cambios en el código...
#    luego preparar los cambios para commit
git add .

# 6️⃣ Guardar cambios con mensaje descriptivo
git commit -m "Agregar nueva funcionalidad X"

# 7️⃣ Subir la branch al remoto (no directamente a main)
git push origin nueva-funcionalidad

# 8️⃣ Abrir un Pull Request en GitHub desde 'nueva-funcionalidad' hacia 'main'
#    - Esto permite que otros revisen y prueben antes de mergear

# 9️⃣ Probar el código localmente si venís de un PR de otro colaborador
git fetch origin pull/ID_DEL_PR/head:branch_prueba
git checkout branch_prueba
# 👈 probás el código sin afectar main

# 🔟 Una vez revisado y aprobado, mergear PR en main
git checkout main
git pull origin main
git merge nueva-funcionalidad  # o usar GitHub para merge vía PR
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

## 👨‍💻 Autores

Roibeth Garcia - [GitHub](https://github.com/RoibethGarcia)
Lucas Machin -[GitHub](https://github.com/lmachin98)

---


