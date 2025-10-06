# 📚 Biblioteca PAP - Sistema de Gestión Completo

## 🚀 INSTRUCCIONES RÁPIDAS (IntelliJ IDEA)

### 1. Configuración Inicial
1) **Open** -> `/Users/roibethgarcia/Projects/biblioteca-pap`
2) **Maven import automático** (IntelliJ detecta pom.xml)
3) **Configura JDK 17** en Project Structure
4) **Run**: `mvn -q -DskipTests exec:java` (Maven) o **Shift+F10** en IntelliJ (MainRefactored.java)

### 2. Base de Datos
#### H2 (Desarrollo - Recomendado):
- ✅ **Configurado por defecto** - No requiere configuración
- Datos en: `./target/h2db/biblioteca_pap`
- Consola web: `java -cp "target/classes:target/dependency/*" org.h2.tools.Console`

#### MySQL (Producción):
- Crea BD: `CREATE DATABASE biblioteca_pap CHARACTER SET utf8mb4;`
- Configura: `src/main/resources/hibernate-mysql.cfg.xml`
- Ejecuta: `java -Ddb=mysql -jar target/biblioteca-pap.jar`

## 🏗️ ESTRUCTURA ACTUAL DEL PROYECTO

```
biblioteca-pap/
├── 📁 src/main/java/edu/udelar/pap/
│   ├── 🎮 controller/                    # Controladores MVC
│   │   ├── MainController.java           # Controlador principal
│   │   ├── LectorController.java         # Gestión de lectores
│   │   ├── BibliotecarioController.java  # Gestión de bibliotecarios
│   │   ├── DonacionController.java       # Gestión de donaciones
│   │   ├── PrestamoController.java       # Gestión de préstamos
│   │   ├── PrestamoUIHelper.java         # Helper UI préstamos
│   │   └── PrestamoValidator.java        # Validador préstamos
│   │
│   ├── 🏗️ domain/                        # Entidades JPA
│   │   ├── Usuario.java                  # Clase base usuarios
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
│   │   ├── LectorService.java            # Servicios lector
│   │   ├── BibliotecarioService.java     # Servicios bibliotecario
│   │   ├── DonacionService.java          # Servicios donación
│   │   ├── PrestamoService.java          # Servicios préstamo
│   │   └── PrestamoServiceRefactored.java # Servicios refactorizados
│   │
│   ├── 🗄️ repository/                    # Acceso a datos
│   │   ├── PrestamoRepository.java       # Interfaz repositorio
│   │   └── impl/
│   │       └── PrestamoRepositoryImpl.java # Implementación
│   │
│   ├── 🖥️ ui/                           # Interfaces Swing
│   │   ├── MainRefactored.java           # Punto entrada principal
│   │   ├── LectorUIUtil.java             # Utilidades UI lector
│   │   ├── ControllerUtil.java           # Utilidades controlador
│   │   ├── DataViewer.java               # Visualizador datos
│   │   ├── ValidacionesUtil.java         # Utilidades validación
│   │   ├── DateTextField.java            # Campo fecha personalizado
│   │   ├── MaterialComboBoxItem.java     # Item combo materiales
│   │   ├── InterfaceUtil.java            # Utilidades interfaz
│   │   ├── DatabaseUtil.java             # Utilidades BD
│   │   ├── DatabaseTester.java           # Tester BD
│   │   ├── SchemaGenerator.java          # Generador esquemas
│   │   └── ConfigChecker.java            # Verificador configuración
│   │
│   ├── 🌐 servlet/                       # Servlets Web
│   │   ├── AuthServlet.java              # Autenticación web
│   │   ├── LectorServlet.java            # Gestión lectores web
│   │   ├── LectorServletRefactored.java  # Servlet refactorizado
│   │   ├── BibliotecarioServlet.java     # Gestión bibliotecarios web
│   │   ├── DonacionServlet.java          # Gestión donaciones web
│   │   ├── PrestamoServlet.java          # Gestión préstamos web
│   │   ├── DashboardServlet.java         # Dashboard web
│   │   ├── ManagementServlet.java        # Gestión usuarios web
│   │   ├── TestServlet.java              # Testing web
│   │   └── handler/
│   │       └── LectorRequestHandler.java # Handler requests
│   │
│   ├── 📡 publisher/                     # Servicios Web (JAX-WS)
│   │   ├── LectorPublisher.java          # Publisher lectores
│   │   └── BibliotecarioPublisher.java   # Publisher bibliotecarios
│   │
│   ├── 🏭 factory/                       # Patrones Factory
│   │   └── ServiceFactory.java           # Factory servicios
│   │
│   ├── 🛠️ util/                         # Utilidades generales
│   │   └── ErrorHandler.java             # Manejador errores
│   │
│   ├── ⚠️ exception/                     # Excepciones personalizadas
│   │   ├── BibliotecaException.java      # Excepción base
│   │   ├── BusinessRuleException.java    # Excepción regla negocio
│   │   └── ValidationException.java      # Excepción validación
│   │
│   ├── 🔧 persistence/                   # Configuración persistencia
│   │   └── HibernateUtil.java            # Utilidad Hibernate
│   │
│   └── 🖥️ server/                        # Servidor integrado
│       └── IntegratedServer.java         # Servidor desarrollo
│
├── 📁 src/main/webapp/                   # Aplicación Web
│   ├── 📄 index.html                     # Página principal
│   ├── 📄 landing.html                   # Landing page
│   ├── 📄 spa.html                       # Single Page Application
│   ├── 📄 test-spa.html                  # Testing SPA
│   │
│   ├── 📁 css/                           # Estilos
│   │   ├── style.css                     # Estilos principales
│   │   ├── spa.css                       # Estilos SPA
│   │   └── landing.css                   # Estilos landing
│   │
│   ├── 📁 js/                            # JavaScript
│   │   ├── main.js                       # JavaScript principal
│   │   ├── api.js                        # API JavaScript
│   │   ├── spa.js                        # Lógica SPA
│   │   ├── dashboard.js                  # Dashboard
│   │   ├── management.js                 # Gestión usuarios
│   │   ├── forms.js                      # Formularios
│   │   ├── landing.js                    # Landing page
│   │   └── lazy-loading.js               # Carga perezosa
│   │
│   └── 📁 WEB-INF/                       # Configuración Web
│       ├── web.xml                       # Descriptor web
│       ├── sun-jaxws.xml                 # Configuración JAX-WS
│       └── jsp/                          # Páginas JSP
│           ├── auth/                     # Autenticación
│           ├── dashboard/                # Dashboard
│           ├── management/               # Gestión
│           └── shared/                   # Componentes compartidos
│
├── 📁 src/main/resources/                # Configuraciones
│   ├── hibernate-h2.cfg.xml              # Config H2 (desarrollo)
│   ├── hibernate-mysql.cfg.xml           # Config MySQL (producción)
│   ├── hibernate-mysql-team.cfg.xml      # Config MySQL equipo
│   └── logging.properties                # Configuración logging
│
├── 📁 documentacion/                     # Documentación completa
│   ├── README.md                         # Documentación principal
│   ├── FUNCIONES_IMPLEMENTADAS.md        # Funciones implementadas
│   ├── FUNCIONALIDAD_*.md                # Documentación funcionalidades
│   ├── REFACTORIZACION_*.md              # Documentación refactorización
│   ├── INSTRUCCIONES_*.md                # Instrucciones migración
│   ├── CONFIGURACION_*.md                # Configuraciones equipo
│   └── WARNINGS_SOLUCIONADOS.md          # Warnings solucionados
│
├── 📁 scripts/                           # Scripts automatización
│   ├── ejecutar-app.sh                   # Ejecutar aplicación
│   ├── ejecutar-servidor-integrado.sh    # Servidor integrado
│   ├── compile-all.sh                    # Compilar todo
│   ├── probar-*.sh                       # Scripts de prueba
│   ├── debug-*.sh                        # Scripts de debug
│   └── recargar-webapp.sh                # Recargar webapp
│
├── 📁 target/                            # Archivos compilados
├── 📄 pom.xml                            # Configuración Maven
├── 📄 README.txt                         # Este archivo
└── 📄 .gitignore                         # Archivos ignorados
```

## ✅ FUNCIONALIDADES IMPLEMENTADAS (Desktop)

### 🎯 **100% COMPLETADO - 15/15 Funcionalidades**
- ✅ **Gestión de Usuarios**: Lectores y Bibliotecarios
- ✅ **Gestión de Materiales**: Libros y Artículos Especiales
- ✅ **Sistema de Donaciones**: Registro completo
- ✅ **Consulta de Donaciones**: Filtros por rango de fechas
- ✅ **Gestión de Préstamos**: Control completo
- ✅ **Edición de Préstamos**: Todos los campos editables
- ✅ **Préstamos por Lector**: Consulta específica
- ✅ **Historial por Bibliotecario**: Auditoría completa
- ✅ **Reporte por Zona**: Análisis territorial
- ✅ **Materiales Pendientes**: Priorización inteligente

## 🌐 FUNCIONALIDADES WEB IMPLEMENTADAS

### ✅ **Autenticación Web**
- Login de usuarios (Lectores y Bibliotecarios)
- Registro de nuevos usuarios
- Gestión de sesiones
- Diferenciación de roles

### ✅ **Dashboard Web**
- Dashboard para Lectores
- Dashboard para Bibliotecarios
- Navegación diferenciada por rol

### ✅ **Gestión de Usuarios Web**
- Listado de lectores
- Gestión básica de usuarios
- Interfaz de administración

### ✅ **Servicios Web (JAX-WS)**
- API REST para lectores
- API REST para bibliotecarios
- Endpoints de autenticación

## ❌ CASOS DE USO WEBAPP FALTANTES POR IMPLEMENTAR

### 🔴 **ALTA PRIORIDAD**

#### 1. **Gestión Completa de Materiales Web**
- ❌ **CRUD de Libros**: Crear, editar, eliminar libros desde web
- ❌ **CRUD de Artículos Especiales**: Gestión completa de artículos
- ❌ **Búsqueda Avanzada**: Filtros por título, autor, categoría, ISBN
- ❌ **Catálogo Público**: Visualización de materiales disponibles
- ❌ **Gestión de Inventario**: Control de stock y disponibilidad

#### 2. **Sistema de Préstamos Web Completo**
- ❌ **Solicitud de Préstamos**: Lectores pueden solicitar materiales
- ❌ **Aprobación de Préstamos**: Bibliotecarios aprueban/rechazan
- ❌ **Gestión de Devoluciones**: Proceso completo de devolución
- ❌ **Renovación de Préstamos**: Extensión de fechas de vencimiento
- ❌ **Historial de Préstamos**: Consulta completa por usuario
- ❌ **Notificaciones**: Alertas de vencimiento y recordatorios

#### 3. **Sistema de Donaciones Web**
- ❌ **Formulario de Donación**: Registro de donaciones desde web
- ❌ **Aprobación de Donaciones**: Workflow de aprobación
- ❌ **Seguimiento de Donaciones**: Estado y trazabilidad
- ❌ **Reportes de Donaciones**: Estadísticas y análisis

### 🟡 **MEDIA PRIORIDAD**

#### 4. **Reportes y Analytics Web**
- ❌ **Dashboard de Estadísticas**: Métricas en tiempo real
- ❌ **Reportes por Período**: Análisis temporal
- ❌ **Reportes por Zona**: Análisis geográfico
- ❌ **Reportes de Uso**: Materiales más prestados
- ❌ **Exportación de Datos**: PDF, Excel, CSV

#### 5. **Gestión Avanzada de Usuarios**
- ❌ **Perfil de Usuario**: Edición de datos personales
- ❌ **Cambio de Contraseña**: Seguridad de cuentas
- ❌ **Recuperación de Contraseña**: Reset por email
- ❌ **Gestión de Estados**: Activar/desactivar usuarios
- ❌ **Historial de Actividad**: Log de acciones del usuario

#### 6. **Sistema de Notificaciones**
- ❌ **Notificaciones Push**: Alertas en tiempo real
- ❌ **Email Notifications**: Recordatorios por correo
- ❌ **SMS Notifications**: Alertas por mensaje de texto
- ❌ **Configuración de Preferencias**: Personalización de notificaciones

### 🟢 **BAJA PRIORIDAD**

#### 7. **Funcionalidades Sociales**
- ❌ **Sistema de Reseñas**: Comentarios sobre materiales
- ❌ **Sistema de Favoritos**: Lista de materiales preferidos
- ❌ **Recomendaciones**: Sugerencias personalizadas
- ❌ **Foro de Discusión**: Comunidad de usuarios

#### 8. **Integración y APIs**
- ❌ **API REST Completa**: Endpoints para todas las funcionalidades
- ❌ **Integración con Sistemas Externos**: APIs de terceros
- ❌ **Webhooks**: Notificaciones a sistemas externos
- ❌ **Sincronización Offline**: Modo sin conexión

#### 9. **Funcionalidades Avanzadas**
- ❌ **Sistema de Reservas**: Reserva de materiales no disponibles
- ❌ **Sistema de Multas**: Gestión automática de penalizaciones
- ❌ **Sistema de Eventos**: Gestión de actividades de la biblioteca
- ❌ **Sistema de Voluntarios**: Gestión de personal voluntario

## 🛠️ TECNOLOGÍAS UTILIZADAS

### **Backend**
- **Java 17** - Lenguaje principal
- **Hibernate 6.x** - ORM para persistencia
- **Maven** - Gestión de dependencias
- **JAX-WS** - Servicios web
- **Servlets** - Manejo de requests HTTP
- **JSP** - Páginas dinámicas

### **Frontend**
- **HTML5/CSS3** - Estructura y estilos
- **JavaScript ES6+** - Lógica del cliente
- **Bootstrap** - Framework CSS (parcial)
- **AJAX** - Comunicación asíncrona
- **SPA Architecture** - Single Page Application

### **Base de Datos**
- **H2 Database** - Desarrollo
- **MySQL** - Producción
- **JPA/Hibernate** - Mapeo objeto-relacional

### **Herramientas**
- **IntelliJ IDEA** - IDE principal
- **Git** - Control de versiones
- **Maven** - Build automation

## 🚀 COMANDOS ÚTILES

### **Ejecución**
```bash
# Aplicación desktop
./scripts/ejecutar-app.sh

# Servidor web integrado
./scripts/ejecutar-servidor-integrado.sh

# Compilar todo
./scripts/compile-all.sh
```

### **Testing**
```bash
# Probar funcionalidades específicas
./scripts/probar-*.sh

# Debug de problemas
./scripts/debug-*.sh
```

### **Desarrollo**
```bash
# Compilar con Maven
mvn clean compile

# Ejecutar tests
mvn test

# Generar JAR
mvn package
```

## 📞 CONTACTO Y SOPORTE

**Desarrollador Principal**: Roibeth Garcia
- **GitHub**: https://github.com/RoibethGarcia
- **Proyecto**: https://github.com/RoibethGarcia/Biblioteca-pap

**Colaborador**: Lucas Machin
- **GitHub**: https://github.com/lucasmachin1234

## 📝 NOTAS IMPORTANTES

1. **JDK 17+** es requerido para ejecutar el proyecto
2. **H2 Database** se configura automáticamente para desarrollo
3. **MySQL** requiere configuración manual para producción
4. **IntelliJ IDEA** es el IDE recomendado
5. **Maven** maneja automáticamente las dependencias
6. **Documentación completa** disponible en `/documentacion/`
7. **Scripts automatizados** disponibles en `/scripts/`

---
**🎉 ¡Proyecto 100% funcional en desktop, webapp en desarrollo activo!**
