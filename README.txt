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

### ✅ **Autenticación Web (2/15 funcionalidades)**
- ✅ **Login de usuarios**: Como bibliotecario o lector, quiero poder hacer login en la aplicación
- ✅ **Diferenciación de roles**: Sistema de autenticación con roles diferenciados
- ✅ **Registro de nuevos usuarios**: Formulario de registro para nuevos usuarios
- ✅ **Gestión de sesiones**: Control de sesiones de usuario
- ✅ **Dashboard diferenciado**: Interfaz adaptada según el rol del usuario

### ✅ **Infraestructura Web**
- ✅ **Servicios Web (JAX-WS)**: API REST para lectores y bibliotecarios
- ✅ **Endpoints de autenticación**: Servicios de login y registro
- ✅ **Interfaz SPA**: Single Page Application con navegación dinámica
- ✅ **Servidor integrado**: Servidor de desarrollo para testing

### 📊 **ESTADO DETALLADO DE IMPLEMENTACIÓN WEB**

| Funcionalidad | Estado | Descripción |
|---------------|--------|-------------|
| **Login de usuarios** | ✅ IMPLEMENTADO | Sistema de autenticación funcional |
| **Diferenciación de roles** | ✅ IMPLEMENTADO | Dashboard diferenciado por rol |
| **Suspensión de lectores** | ❌ FALTANTE | Cambiar estado a SUSPENDIDO |
| **Cambio de zona** | ❌ FALTANTE | Modificar barrio del lector |
| **Registro de donación libros** | ❌ FALTANTE | Formulario de donación de libros |
| **Registro de donación artículos** | ❌ FALTANTE | Formulario de donación de artículos |
| **Consulta de donaciones** | ❌ FALTANTE | Listado de todas las donaciones |
| **Consulta por rango fechas** | ❌ FALTANTE | Filtro de donaciones por período |
| **Creación de préstamos** | ❌ FALTANTE | Formulario de nuevo préstamo |
| **Actualización estado préstamos** | ❌ FALTANTE | Cambiar estado EN CURSO/DEVUELTO |
| **Consulta préstamos por estado** | ❌ FALTANTE | Ver préstamos agrupados por estado |
| **Actualización completa préstamos** | ❌ FALTANTE | Editar cualquier campo de préstamo |
| **Listado préstamos activos** | ❌ FALTANTE | Préstamos activos por lector |
| **Historial por bibliotecario** | ❌ FALTANTE | Préstamos gestionados por bibliotecario |
| **Reporte por zona** | ❌ FALTANTE | Análisis de préstamos por barrio |
| **Materiales pendientes** | ❌ FALTANTE | Identificar materiales con muchos préstamos |

## ❌ CASOS DE USO WEBAPP FALTANTES POR IMPLEMENTAR

### 📋 **HISTORIAS DE USUARIO - FUNCIONALIDADES MÍNIMAS REQUERIDAS**

#### 1. **👥 Gestión de Usuarios**

##### ✅ **IMPLEMENTADO**
- ✅ **Login de usuarios**: Como bibliotecario o lector, quiero poder hacer login en la aplicación
- ✅ **Diferenciación de roles**: Sistema de autenticación con roles diferenciados

##### ❌ **FALTANTE POR IMPLEMENTAR**
- ❌ **Suspensión de lectores**: Como bibliotecario, quiero modificar el estado de un lector a "SUSPENDIDO" para impedirle realizar nuevos préstamos si incumple con las normas
- ❌ **Cambio de zona**: Como bibliotecario quiero cambiar el barrio (zona) de un lector para mantener actualizada su ubicación dentro del sistema

#### 2. **📚 Gestión de Materiales**

##### ❌ **FALTANTE POR IMPLEMENTAR**
- ❌ **Registro de donación de libros**: Como bibliotecario, quiero registrar una nueva donación de libros indicando su título y cantidad de páginas para incorporar al inventario
- ❌ **Registro de donación de artículos especiales**: Como bibliotecario, quiero registrar una nueva donación de artículo especial con su descripción, peso y dimensiones para que esté disponible para préstamo
- ❌ **Consulta de donaciones**: Como bibliotecario y lector quiero consultar todas las donaciones registradas
- ❌ **Consulta de donaciones por rango de fechas** (OPCIONAL): Como bibliotecario, quiero consultar todas las donaciones registradas en un rango de fechas para tener trazabilidad del inventario

#### 3. **📖 Gestión de Préstamos**

##### ❌ **FALTANTE POR IMPLEMENTAR**
- ❌ **Creación de préstamos**: Como lector, quiero crear un nuevo préstamo asociando un material a un lector y a un bibliotecario, para registrar el movimiento del material
- ❌ **Actualización de estado de préstamos**: Como bibliotecario, quiero actualizar el estado de un préstamo a "EN CURSO" o "DEVUELTO" para reflejar su progreso
- ❌ **Consulta de préstamos por estado**: Como lector quiero ver todas mis préstamos agrupados por estado
- ❌ **Actualización completa de préstamos** (OPCIONAL): Como bibliotecario, quiero actualizar cualquier información de un préstamo
- ❌ **Listado de préstamos activos por lector** (OPCIONAL): Como bibliotecario quiero listar todos los préstamos activos de un lector para verificar su historial y controlar el cumplimiento de devoluciones

#### 4. **📊 Control y Seguimiento**

##### ❌ **FALTANTE POR IMPLEMENTAR (TODOS OPCIONALES)**
- ❌ **Historial de préstamos por bibliotecario** (OPCIONAL): Como bibliotecario quiero ver el historial de préstamos gestionados por mi
- ❌ **Reporte de préstamos por zona** (OPCIONAL): Como bibliotecario, quiero obtener un reporte de préstamos por zona para analizar el uso del servicio en diferentes barrios
- ❌ **Identificación de materiales pendientes** (OPCIONAL): Como bibliotecario, quiero identificar materiales con muchos préstamos pendientes para priorizar su devolución o reposición

### 🎯 **RESUMEN DE IMPLEMENTACIÓN WEBAPP**

| Categoría | Funcionalidades | Implementadas | Faltantes | Total |
|-----------|----------------|---------------|-----------|-------|
| **Gestión de Usuarios** | 3 | 2 | 1 | 3 |
| **Gestión de Materiales** | 4 | 0 | 4 | 4 |
| **Gestión de Préstamos** | 5 | 0 | 5 | 5 |
| **Control y Seguimiento** | 3 | 0 | 3 | 3 |
| **TOTAL** | **15** | **2** | **13** | **15** |

### 📈 **ESTADO ACTUAL WEBAPP**
- ✅ **Implementado**: 2/15 funcionalidades (13.3%)
- ❌ **Faltante**: 13/15 funcionalidades (86.7%)
- 🎯 **Progreso**: Sistema básico de autenticación implementado

### 🔴 **PRIORIDADES DE IMPLEMENTACIÓN**

#### **FASE 1 - FUNCIONALIDADES MÍNIMAS (ALTA PRIORIDAD)**
1. **Gestión de usuarios restante** (2 funcionalidades)
2. **Gestión de materiales completa** (4 funcionalidades)
3. **Gestión de préstamos básica** (3 funcionalidades principales)

#### **FASE 2 - FUNCIONALIDADES OPCIONALES (MEDIA PRIORIDAD)**
4. **Gestión de préstamos avanzada** (2 funcionalidades opcionales)
5. **Control y seguimiento** (3 funcionalidades opcionales)

### 🛠️ **FUNCIONALIDADES ADICIONALES IDENTIFICADAS (NO REQUERIDAS)**

#### **🟡 MEJORAS DE USABILIDAD**
- ❌ **Perfil de usuario**: Edición de datos personales
- ❌ **Cambio de contraseña**: Seguridad de cuentas
- ❌ **Recuperación de contraseña**: Reset por email
- ❌ **Dashboard personalizado**: Interfaz adaptada por rol

#### **🟢 FUNCIONALIDADES AVANZADAS**
- ❌ **Sistema de notificaciones**: Alertas y recordatorios
- ❌ **Búsqueda avanzada**: Filtros múltiples
- ❌ **Exportación de datos**: PDF, Excel, CSV
- ❌ **API REST completa**: Integración con sistemas externos

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

## 🎯 **RESUMEN EJECUTIVO**

### **📊 Estado del Proyecto**
- **🖥️ Aplicación Desktop**: ✅ **100% COMPLETADA** (15/15 funcionalidades)
- **🌐 Aplicación Web**: ⚠️ **13.3% COMPLETADA** (2/15 funcionalidades)

### **🚀 Próximos Pasos Recomendados**
1. **Implementar gestión de usuarios restante** (suspensión y cambio de zona)
2. **Desarrollar gestión completa de materiales** (donaciones de libros y artículos)
3. **Crear sistema de préstamos web** (creación, actualización y consulta)
4. **Implementar funcionalidades opcionales** (reportes y control avanzado)

### **📈 Progreso General**
- **Total de funcionalidades**: 30 (15 desktop + 15 web)
- **Implementadas**: 17 (15 desktop + 2 web)
- **Faltantes**: 13 (0 desktop + 13 web)
- **Progreso general**: 56.7% completado

---
**🎉 ¡Proyecto desktop 100% funcional, webapp con base sólida para desarrollo!**
