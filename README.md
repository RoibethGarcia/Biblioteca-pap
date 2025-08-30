# 📚 Biblioteca PAP - Sistema de Gestión

Sistema de gestión de biblioteca desarrollado en Java con Hibernate y Swing para la interfaz de usuario.

## 🚀 Características

- **Gestión de Usuarios**: Lectores y Bibliotecarios
- **Gestión de Materiales**: Libros y Artículos Especiales
- **Sistema de Donaciones**: Registro de materiales donados
- **Consulta de Donaciones**: Filtros por rango de fechas para trazabilidad
- **Gestión de Préstamos**: Control de préstamos de materiales
- **Base de Datos**: H2 (desarrollo) y MySQL (producción)

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Hibernate 6.x** (ORM)
- **H2 Database** (desarrollo)
- **MySQL** (producción)
- **Maven** (gestión de dependencias)
- **Swing** (interfaz gráfica)

## 🎯 Estado del Proyecto

### ✅ **IMPLEMENTACIÓN COMPLETA**

**🎉 ¡PROYECTO COMPLETAMENTE IMPLEMENTADO!**

- **✅ Funcionalidades Mínimas**: 9/9 COMPLETADAS
- **✅ Funcionalidades Opcionales**: 6/6 COMPLETADAS
- **✅ Total de Funcionalidades**: 15/15 IMPLEMENTADAS

### 📊 Resumen de Implementación

| Categoría | Funcionalidades | Estado |
|-----------|----------------|---------|
| **Gestión de Usuarios** | 4 funcionalidades | ✅ COMPLETADO |
| **Gestión de Materiales** | 4 funcionalidades | ✅ COMPLETADO |
| **Gestión de Préstamos** | 5 funcionalidades | ✅ COMPLETADO |
| **Control y Seguimiento** | 2 funcionalidades | ✅ COMPLETADO |

### 🏆 Logros Alcanzados

- **100% de funcionalidades mínimas** implementadas
- **100% de funcionalidades opcionales** implementadas
- **Sistema completo** de gestión de biblioteca
- **Interfaz moderna** y funcional
- **Documentación completa** de todas las funcionalidades
- **Scripts de prueba** para cada funcionalidad

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

### Método 1: Script Automático (Recomendado)
```bash
./ejecutar-app.sh
```

### Método 2: Desde IntelliJ IDEA
1. Abrir `src/main/java/edu/udelar/pap/ui/MainRefactored.java`
2. Presionar **Shift + F10** (Run)

### Método 3: Desde Línea de Comandos
```bash
# Con Maven (configurado automáticamente)
mvn -q -DskipTests exec:java

# Con Java directo (después de compilar)
java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.MainRefactored
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

-- Consultar donaciones por rango de fechas
SELECT * FROM LIBRO WHERE FECHA_INGRESO BETWEEN '2024-01-01' AND '2024-12-31';
SELECT * FROM ARTICULOS_ESPECIALES WHERE FECHA_INGRESO BETWEEN '2024-01-01' AND '2024-12-31';
```

## 🆕 Nuevas Funcionalidades

### 📅 Consulta de Donaciones por Rango de Fechas
- **Acceso**: Menú → Materiales → Consultar Donaciones
- **Funcionalidad**: Filtrar donaciones por período específico
- **Formato**: DD/MM/AAAA (ejemplo: 01/01/2024 a 31/12/2024)
- **Beneficios**: Trazabilidad temporal del inventario

### ✏️ Edición Completa de Préstamos
- **Acceso**: Menú → Préstamos → Gestionar Devoluciones
- **Funcionalidad**: Editar cualquier campo de un préstamo existente
- **Campos editables**: Lector, Bibliotecario, Material, Fecha devolución, Estado
- **Beneficios**: Flexibilidad total en la gestión de préstamos

### 📚 Préstamos Activos por Lector
- **Acceso**: Menú → Préstamos → Préstamos por Lector
- **Funcionalidad**: Consultar y gestionar préstamos activos de un lector específico
- **Características**: Tabla detallada, estadísticas, acciones completas
- **Beneficios**: Control granular y seguimiento de cumplimiento

### 📊 Historial por Bibliotecario
- **Acceso**: Menú → Préstamos → Historial por Bibliotecario
- **Funcionalidad**: Auditar actividad y rendimiento de préstamos por bibliotecario
- **Características**: Historial completo, estadísticas de rendimiento, análisis de productividad
- **Beneficios**: Auditoría de personal y control de calidad

### 🗺️ Reporte por Zona
- **Acceso**: Menú → Préstamos → Reporte por Zona
- **Funcionalidad**: Analizar uso del servicio de préstamos por zona geográfica
- **Características**: Reporte territorial, estadísticas por ubicación, análisis de distribución
- **Beneficios**: Planificación territorial y optimización de recursos

### 📋 Materiales Pendientes
- **Acceso**: Menú → Préstamos → Materiales Pendientes
- **Funcionalidad**: Identificar y priorizar materiales con préstamos pendientes
- **Características**: Ranking inteligente, sistema de priorización, análisis de demanda
- **Beneficios**: Optimización de inventario y mejora de satisfacción del usuario

Para más detalles, ver: [FUNCIONALIDAD_RANGO_FECHAS.md](FUNCIONALIDAD_RANGO_FECHAS.md) | [FUNCIONALIDAD_EDICION_PRESTAMOS.md](FUNCIONALIDAD_EDICION_PRESTAMOS.md) | [FUNCIONALIDAD_PRESTAMOS_POR_LECTOR.md](FUNCIONALIDAD_PRESTAMOS_POR_LECTOR.md) | [FUNCIONALIDAD_HISTORIAL_POR_BIBLIOTECARIO.md](FUNCIONALIDAD_HISTORIAL_POR_BIBLIOTECARIO.md) | [FUNCIONALIDAD_REPORTE_POR_ZONA.md](FUNCIONALIDAD_REPORTE_POR_ZONA.md) | [FUNCIONALIDAD_MATERIALES_PENDIENTES.md](FUNCIONALIDAD_MATERIALES_PENDIENTES.md)

## 📁 Estructura del Proyecto

```
biblioteca-pap/
├── 📁 src/main/java/edu/udelar/pap/
│   ├── 🎮 controller/                    # Controladores MVC
│   │   ├── MainController.java           # Controlador principal
│   │   ├── ControllerFactory.java        # Factory de controladores
│   │   ├── LectorController.java         # Gestión de lectores
│   │   ├── BibliotecarioController.java  # Gestión de bibliotecarios
│   │   ├── DonacionController.java       # Gestión de donaciones
│   │   └── PrestamoControllerUltraRefactored.java # Gestión de préstamos
│   │
│   ├── 🏗️ domain/                        # Entidades JPA
│   │   ├── Usuario.java                  # Clase base de usuarios
│   │   ├── Lector.java                   # Entidad lector
│   │   ├── Bibliotecario.java            # Entidad bibliotecario
│   │   ├── DonacionMaterial.java         # Entidad donación
│   │   ├── Libro.java                    # Entidad libro
│   │   ├── ArticuloEspecial.java         # Entidad artículo especial
│   │   ├── Prestamo.java                 # Entidad préstamo
│   │   ├── EstadoLector.java             # Enum estado lector
│   │   ├── EstadoPrestamo.java           # Enum estado préstamo
│   │   └── Zona.java                     # Enum zona
│   │
│   ├── ⚙️ service/                       # Lógica de negocio
│   │   ├── LectorService.java            # Servicios de lector
│   │   ├── BibliotecarioService.java     # Servicios de bibliotecario
│   │   ├── DonacionService.java          # Servicios de donación
│   │   └── PrestamoService.java          # Servicios de préstamo
│   │
│   ├── 🗄️ repository/                    # Acceso a datos
│   │   ├── LectorRepository.java         # Interfaz repositorio
│   │   └── impl/
│   │       └── LectorRepositoryImpl.java # Implementación repositorio
│   │
│   ├── 🖥️ ui/                           # Interfaces de usuario
│   │   ├── MainRefactored.java           # Punto de entrada principal
│   │   ├── LectorUIUtil.java             # Utilidades UI lector
│   │   ├── PrestamoUIUtil.java           # Utilidades UI préstamo
│   │   ├── ControllerUtil.java           # Utilidades controlador
│   │   ├── DataViewer.java               # Visualizador de datos
│   │   ├── ValidacionesUtil.java         # Utilidades validación
│   │   ├── DateTextField.java            # Campo fecha personalizado
│   │   ├── MaterialComboBoxItem.java     # Item combo materiales
│   │   ├── InterfaceUtil.java            # Utilidades interfaz
│   │   ├── DatabaseUtil.java             # Utilidades base de datos
│   │   ├── DatabaseTester.java           # Tester de base de datos
│   │   ├── SchemaGenerator.java          # Generador de esquemas
│   │   └── ConfigChecker.java            # Verificador de configuración
│   │
│   ├── 🛠️ util/                         # Utilidades generales
│   │   └── ErrorHandler.java             # Manejador de errores
│   │
│   ├── ⚠️ exception/                     # Excepciones personalizadas
│   │   ├── BibliotecaException.java      # Excepción base
│   │   ├── BusinessRuleException.java    # Excepción regla negocio
│   │   └── ValidationException.java      # Excepción validación
│   │
│   └── 🔧 persistence/                   # Configuración persistencia
│       └── HibernateUtil.java            # Utilidad Hibernate
│
├── 📁 src/main/resources/                # Configuraciones
│   ├── hibernate-h2.cfg.xml              # Config H2 (desarrollo)
│   ├── hibernate-mysql.cfg.xml           # Config MySQL (producción)
│   ├── hibernate-mysql-team.cfg.xml      # Config MySQL equipo
│   └── logging.properties                # Configuración logging
│
├── 📁 target/                           # Archivos compilados
├── 📁 logs/                             # Archivos de log
├── 📁 .git/                             # Control de versiones
├── 📁 .idea/                            # Configuración IntelliJ
├── 📁 .vscode/                          # Configuración VS Code
│
├── 📄 pom.xml                           # Configuración Maven
├── 📄 README.md                         # Documentación principal
├── 📄 README.txt                        # Instrucciones rápidas
├── 📄 .gitignore                        # Archivos ignorados
├── 📄 ejecutar-app.sh                   # Script de ejecución
│
├── 📄 apache-maven-3.9.6-bin.tar.gz     # Maven (opcional)
│
└── 📄 Scripts de prueba y documentación
    ├── FUNCIONES_IMPLEMENTADAS.md
    ├── FUNCIONALIDAD_*.md               # Documentación funcionalidades
    ├── REFACTORIZACION_*.md             # Documentación refactorización
    ├── INSTRUCCIONES_*.md               # Instrucciones migración
    ├── CONFIGURACION_*.md               # Configuraciones equipo
    ├── WARNINGS_SOLUCIONADOS.md
    ├── test-*.sh                        # Scripts de prueba
    ├── migrar-*.sh                      # Scripts migración
    └── compile-all.sh                   # Script compilación
```

## 👥 Autores del Proyecto

### 🎯 **Equipo de Desarrollo**

**Roibeth Garcia** - [GitHub](https://github.com/RoibethGarcia)
- Desarrollador principal
- Arquitectura del sistema
- Implementación de funcionalidades core
- Refactorización y optimización

**Lucas Machin** - [GitHub](https://github.com/lucasmachin1234)
- Desarrollador colaborador
- Implementación de funcionalidades adicionales
- Testing y validación
- Documentación técnica

### 🤝 **Colaboración**
Este proyecto es el resultado de la colaboración entre ambos desarrolladores, implementando un sistema completo de gestión de biblioteca comunitaria con todas las funcionalidades requeridas.

## 🔧 Solución de Problemas

### Error: "illegal component position"
- ✅ **SOLUCIONADO**: El mensaje de bienvenida ahora se centra correctamente
- La aplicación se ejecuta sin problemas

### Error de Compilación
- Verificar que Java 17+ esté instalado: `java -version`
- Limpiar y recompilar: `mvn clean compile`

### Error de Base de Datos
- H2 se crea automáticamente en `./target/h2db/`
- Verificar permisos de escritura en el directorio

## 📝 Licencia

Este proyecto es parte del curso PAP (Programación Avanzada y Persistencia).

## 🎉 ¡Listo para Usar!

La aplicación está completamente funcional y lista para gestionar una biblioteca comunitaria. El mensaje de bienvenida se centra correctamente en cualquier resolución de pantalla.

## 📝 Cambios Recientes

### ✅ **Última Actualización (Agosto 2025)**
- **🔧 Solucionado**: Error "illegal component position" en el mensaje de bienvenida
- **🎯 Mejorado**: Centrado dinámico del mensaje de bienvenida para pantalla completa
- **📚 Actualizado**: Estructura completa del proyecto en el README
- **👥 Agregado**: Información de ambos autores del equipo
- **⚙️ Optimizado**: Configuración del exec-maven-plugin para ejecución automática
- **📖 Mejorado**: Documentación y scripts de ejecución

### 🚀 **Funcionalidades Implementadas**
- ✅ Sistema completo de gestión de biblioteca
- ✅ Interfaz moderna y responsive
- ✅ Base de datos H2 y MySQL
- ✅ Todas las funcionalidades mínimas y opcionales
- ✅ Documentación completa
- ✅ Scripts de prueba automatizados
