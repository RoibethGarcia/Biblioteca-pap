# Sistema de Biblioteca PAP

Sistema de gestión de biblioteca desarrollado en Java con Hibernate para la persistencia de datos.

## 📋 Descripción

Este proyecto implementa un sistema completo de gestión de biblioteca que permite:
- Gestión de usuarios (Lectores y Bibliotecarios)
- Administración de libros y artículos especiales
- Control de préstamos y devoluciones
- Gestión de donaciones de material
- Organización por zonas de la biblioteca

## 🛠️ Tecnologías Utilizadas

- **Java** - Lenguaje principal
- **Hibernate** - ORM para persistencia de datos
- **Maven** - Gestión de dependencias
- **H2 Database** - Base de datos embebida para desarrollo
- **MySQL** - Base de datos para producción

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/edu/udelar/pap/
│   │   ├── domain/          # Entidades del dominio
│   │   ├── persistence/     # Configuración de Hibernate
│   │   └── ui/             # Interfaz de usuario
│   └── resources/
│       ├── hibernate-h2.cfg.xml    # Configuración H2
│       └── hibernate-mysql.cfg.xml # Configuración MySQL
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java JDK 8 o superior
- Maven 3.6+
- IntelliJ IDEA (recomendado)

### Pasos de instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/RoibethGarcia/biblioteca-pap.git
   cd biblioteca-pap
   ```

2. **Abrir en IntelliJ IDEA**
   - Abrir IntelliJ IDEA
   - Seleccionar "Open" y navegar al directorio del proyecto
   - IntelliJ detectará automáticamente que es un proyecto Maven

3. **Configurar la base de datos**
   - Para desarrollo: Usar configuración H2 (incluida)
   - Para producción: Configurar MySQL y usar `hibernate-mysql.cfg.xml`

4. **Compilar y ejecutar**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.Main"
   ```

## 📊 Entidades del Sistema

### Usuarios
- **Lector**: Usuarios que pueden solicitar préstamos
- **Bibliotecario**: Personal administrativo con permisos extendidos

### Material
- **Libro**: Material principal de la biblioteca
- **Artículo Especial**: Materiales especiales (revistas, CDs, etc.)

### Gestión
- **Préstamo**: Control de préstamos y devoluciones
- **Donación**: Registro de donaciones de material
- **Zona**: Organización física de la biblioteca

## 🔧 Configuración de Base de Datos

### H2 (Desarrollo)
El proyecto incluye configuración automática para H2 Database, ideal para desarrollo y pruebas.

### MySQL (Producción)
1. Crear base de datos MySQL
2. Modificar `hibernate-mysql.cfg.xml` con credenciales
3. Ejecutar scripts de inicialización

## 👥 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

**Roibeth Garcia**
- GitHub: [@RoibethGarcia](https://github.com/RoibethGarcia)
- LinkedIn: [in/roibeth-garcia-9aa334253](https://linkedin.com/in/roibeth-garcia-9aa334253)

## 🤝 Agradecimientos

- Universidad de la República (UdelaR)
- Profesores del curso PAP
- Compañeros de equipo
