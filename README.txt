📚 Biblioteca PAP - Sistema de Gestión
Sistema de gestión de biblioteca desarrollado en Java con Hibernate. Incluye interfaz de escritorio (Swing), aplicación web (SPA) con API REST, servicios SOAP (JAX-WS) y servidor HTTP integrado.

🚀 Características
    Aplicación de escritorio (Swing)
    Gestión de Usuarios: Lectores y Bibliotecarios
    Gestión de Materiales: Libros y Artículos Especiales
    Sistema de Donaciones: Registro de materiales donados
    Consulta de Donaciones: Filtros por rango de fechas para trazabilidad
    Gestión de Préstamos: Control completo de préstamos (crear, editar, devoluciones, reportes)
    Aplicación web
    Autenticación: Login, registro y sesiones con roles (lector / bibliotecario)
    Dashboard diferenciado: Interfaz según rol del usuario
    SPA: Single Page Application con navegación dinámica (spa.html)
    API REST: Endpoints en /api/, /auth/, /lector/, /prestamo/, /donacion/, /bibliotecario/
    Servicios SOAP (JAX-WS): Bibliotecario, Lector, Préstamo y Donación con WSDL generado
    Servidor integrado: HTTP en puerto 8080 (sin contenedor externo en modo desarrollo)
    Base de Datos: H2 (desarrollo) y MySQL (producción)
🛠️ Tecnologías Utilizadas
    Java 21
    Hibernate 6.x (ORM)
    JPA (Jakarta Persistence)
    H2 Database (desarrollo)
    MySQL (producción)
    Maven (gestión de dependencias, packaging WAR)
    Swing (interfaz de escritorio)
    Servlets / JSP (aplicación web)
    JAX-WS (Metro) (servicios SOAP con WSDL)
    BCrypt (hash de contraseñas)
    Servidor HTTP integrado (Java HttpServer, puerto 8080)
🎯 Estado del Proyecto
✅ IMPLEMENTACIÓN COMPLETA
🎉 ¡PROYECTO COMPLETAMENTE IMPLEMENTADO!

✅ Funcionalidades Mínimas: 9/9 COMPLETADAS
✅ Funcionalidades Opcionales: 6/6 COMPLETADAS
✅ Total de Funcionalidades: 15/15 IMPLEMENTADAS

📊 Resumen de Implementación
    Categoría	Funcionalidades	Estado
    Gestión de Usuarios	4 funcionalidades	✅ COMPLETADO
    Gestión de Materiales	4 funcionalidades	✅ COMPLETADO
    Gestión de Préstamos	5 funcionalidades	✅ COMPLETADO
    Control y Seguimiento	2 funcionalidades	✅ COMPLETADO
🏆 Logros Alcanzados
    100% de funcionalidades mínimas implementadas
    100% de funcionalidades opcionales implementadas
    Sistema completo de gestión de biblioteca
    Interfaz moderna y funcional
    Documentación completa de todas las funcionalidades
    Scripts de prueba para cada funcionalidad
📋 Requisitos Previos
    Java JDK 21 o superior
    Maven 3.6+ (opcional; IntelliJ importa el proyecto automáticamente)
    Git
🔧 Instalación y Configuración
    1. Clonar el repositorio
    git clone https://github.com/RoibethGarcia/biblioteca-pap.git
    cd biblioteca-pap
    2. Abrir en IntelliJ IDEA
    File → Open → Seleccionar carpeta biblioteca-pap
    Import Maven project (automático)
    Configurar JDK 21 en Project Structure si no está configurado
    3. Configurar Base de Datos
    Opción A: H2 (Desarrollo - Recomendado)
✅ Configurado por defecto
    Los datos se guardan en ./target/h2db/biblioteca_pap
    No requiere configuración adicional
    Opción B: MySQL (Producción)
    Crear base de datos:
    CREATE DATABASE biblioteca_pap CHARACTER SET utf8mb4;
    Configurar conexión:
    
    Editar src/main/resources/hibernate-mysql.cfg.xml
    Ajustar usuario y contraseña
    Ejecutar con MySQL (variable de entorno):
    
    java -Ddb=mysql -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.MainRefactored
    Para despliegue en contenedor (WAR): desplegar target/biblioteca-pap-*.war y configurar el datasource según el servidor.

🎯 Ejecutar la Aplicación
    La aplicación admite tres modos desde MainRefactored (clase principal).
    
    Modo 1: Aplicación de escritorio (por defecto)
    Inicia la interfaz Swing y, automáticamente, el servidor HTTP (puerto 8080) y los servicios SOAP (puertos 9001–9004).
    
    Script (recomendado):
    
    ./scripts/ejecutar-app.sh
    (En Windows: scripts\ejecutar-app.bat)
    
    Desde IntelliJ:
    Abrir src/main/java/edu/udelar/pap/ui/MainRefactored.java → Run (Shift+F10).
    
    Maven:
    
    mvn -q -DskipTests exec:java
    Modo 2: Solo servidor web (sin Swing)
    Solo servidor HTTP en puerto 8080 (landing, SPA, API REST).
    
    mvn -q -DskipTests exec:java -Dexec.args="--server"
    O con Java: java -cp "target/classes:target/dependency/*" edu.udelar.pap.ui.MainRefactored --server
    
    Script: ./scripts/ejecutar-servidor-integrado.sh
    
    URLs:
    
    Landing: http://localhost:8080/landing.html
    SPA: http://localhost:8080/spa.html
    API: http://localhost:8080/api/
    Modo 3: Solo servicios SOAP (WSDL)
    Solo publicación de servicios JAX-WS (sin UI ni servidor web).
    
    mvn -q -DskipTests exec:java -Dexec.args="--soap"
    WSDLs:
    
    http://localhost:9001/BibliotecarioWS?wsdl
    http://localhost:9002/LectorWS?wsdl
    http://localhost:9003/PrestamoWS?wsdl
    http://localhost:9004/DonacionWS?wsdl
    Ayuda
    mvn -q exec:java -Dexec.args="--help"
📊 Verificar Datos en la Base de Datos
Consola H2 Web
Ejecutar: java -cp "target/classes:target/dependency/*" org.h2.tools.Console
Abrir navegador en: http://localhost:8082
Configuración:
JDBC URL: jdbc:h2:./target/h2db/biblioteca_pap
Usuario: sa
Contraseña: (dejar vacío)
Consultas SQL Ejemplo
-- Ver todos los lectores
SELECT * FROM LECTOR;

-- Ver todos los libros
SELECT * FROM LIBRO;

-- Ver préstamos activos
SELECT * FROM PRESTAMO WHERE ESTADO = 'ACTIVO';

-- Consultar donaciones por rango de fechas
    SELECT * FROM LIBRO WHERE FECHA_INGRESO BETWEEN '2024-01-01' AND '2024-12-31';
    SELECT * FROM ARTICULOS_ESPECIALES WHERE FECHA_INGRESO BETWEEN '2024-01-01' AND '2024-12-31';
    🆕 Nuevas Funcionalidades
    📅 Consulta de Donaciones por Rango de Fechas
    Acceso: Menú → Materiales → Consultar Donaciones
    Funcionalidad: Filtrar donaciones por período específico
    Formato: DD/MM/AAAA (ejemplo: 01/01/2024 a 31/12/2024)
    Beneficios: Trazabilidad temporal del inventario
✏️ Edición Completa de Préstamos
    Acceso: Menú → Préstamos → Gestionar Devoluciones
    Funcionalidad: Editar cualquier campo de un préstamo existente
    Campos editables: Lector, Bibliotecario, Material, Fecha devolución, Estado
    Beneficios: Flexibilidad total en la gestión de préstamos
📚 Préstamos Activos por Lector
    Acceso: Menú → Préstamos → Préstamos por Lector
    Funcionalidad: Consultar y gestionar préstamos activos de un lector específico
    Características: Tabla detallada, estadísticas, acciones completas
    Beneficios: Control granular y seguimiento de cumplimiento
📊 Historial por Bibliotecario
    Acceso: Menú → Préstamos → Historial por Bibliotecario
    Funcionalidad: Auditar actividad y rendimiento de préstamos por bibliotecario
    Características: Historial completo, estadísticas de rendimiento, análisis de productividad
    Beneficios: Auditoría de personal y control de calidad
🗺️ Reporte por Zona
    Acceso: Menú → Préstamos → Reporte por Zona
    Funcionalidad: Analizar uso del servicio de préstamos por zona geográfica
    Características: Reporte territorial, estadísticas por ubicación, análisis de distribución
    Beneficios: Planificación territorial y optimización de recursos
📋 Materiales Pendientes
    Acceso: Menú → Préstamos → Materiales Pendientes
    Funcionalidad: Identificar y priorizar materiales con préstamos pendientes
    Características: Ranking inteligente, sistema de priorización, análisis de demanda
    Beneficios: Optimización de inventario y mejora de satisfacción del usuario
    Para más detalles, ver: FUNCIONALIDAD_RANGO_FECHAS.md | FUNCIONALIDAD_EDICION_PRESTAMOS.md | FUNCIONALIDAD_PRESTAMOS_POR_LECTOR.md | FUNCIONALIDAD_HISTORIAL_POR_BIBLIOTECARIO.md | FUNCIONALIDAD_REPORTE_POR_ZONA.md | FUNCIONALIDAD_MATERIALES_PENDIENTES.md | FUNCIONALIDAD_CONSULTA_DONACIONES_POR_FECHA.md | WEB_SERVICES_SOAP_IMPLEMENTADOS.md

📁 Estructura del Proyecto
biblioteca-pap/
├── 📁 src/main/java/edu/udelar/pap/
│   ├── 🎮 controller/                    # Controladores MVC
│   │   ├── MainController.java           # Controlador principal
│   │   ├── ControllerFactory.java        # Factory de controladores
│   │   ├── LectorController.java         # Gestión de lectores
│   │   ├── BibliotecarioController.java  # Gestión de bibliotecarios
│   │   ├── DonacionController.java       # Gestión de donaciones
│   │   ├── PrestamoController.java       # Gestión de préstamos (legacy)
│   │   ├── PrestamoControllerUltraRefactored.java
│   │   ├── PrestamoUIHelper.java         # Helper UI préstamos
│   │   └── PrestamoValidator.java        # Validador préstamos
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
│   │   ├── PrestamoService.java          # Servicios de préstamo
│   │   ├── PrestamoServiceRefactored.java # Servicios refactorizados
│   │   └── AutenticacionService.java     # Login y sesiones
│   │
│   ├── 🗄️ repository/                    # Acceso a datos
│   │   ├── LectorRepository.java         # Interfaz repositorio lectores
│   │   ├── PrestamoRepository.java       # Interfaz repositorio préstamos
│   │   └── impl/
│   │       ├── LectorRepositoryImpl.java
│   │       └── PrestamoRepositoryImpl.java
│   │
│   ├── 🖥️ ui/                            # Interfaz Swing (escritorio)
│   │   ├── MainRefactored.java           # Punto de entrada principal
│   │   ├── LectorUIUtil.java             # Utilidades UI lector
│   │   ├── PrestamoUIUtil.java           # Utilidades UI préstamo
│   │   ├── DataViewer.java               # Visualizador de datos
│   │   ├── DateTextField.java            # Campo fecha personalizado
│   │   └── MaterialComboBoxItem.java     # Item combo materiales
│   │
│   ├── 🌐 server/                        # Servidor web integrado
│   │   └── IntegratedServer.java         # HTTP en puerto 8080 (REST + estáticos)
│   │
│   ├── 🖥️ servlet/                       # Servlets (despliegue WAR)
│   │   ├── AuthServlet.java              # Autenticación
│   │   ├── LectorServlet.java            # API lectores
│   │   ├── LectorServletRefactored.java
│   │   ├── BibliotecarioServlet.java     # API bibliotecarios
│   │   ├── DonacionServlet.java          # API donaciones
│   │   ├── PrestamoServlet.java          # API préstamos
│   │   ├── DashboardServlet.java         # Dashboard
│   │   ├── ManagementServlet.java        # Gestión usuarios
│   │   ├── TestServlet.java
│   │   └── handler/
│   │       └── LectorRequestHandler.java
│   │
│   ├── 📡 webservice/                    # Servicios SOAP (JAX-WS)
│   │   ├── BibliotecarioWebService.java  # Interface + Impl
│   │   ├── LectorWebService.java
│   │   ├── PrestamoWebService.java
│   │   └── DonacionWebService.java
│   │
│   ├── 📡 publisher/                      # Publicadores SOAP
│   │   ├── PublisherFactory.java
│   │   ├── BibliotecarioPublisher.java
│   │   ├── LectorPublisher.java
│   │   ├── PrestamoPublisher.java
│   │   └── DonacionPublisher.java
│   │
│   ├── 🏭 factory/
│   │   └── ServiceFactory.java           # Factory de servicios
│   │
│   ├── 🛠️ util/                         # Utilidades generales
│   │   ├── ErrorHandler.java             # Manejador de errores
│   │   ├── ControllerUtil.java           # Utilidades controlador
│   │   ├── ValidacionesUtil.java         # Validaciones
│   │   ├── InterfaceUtil.java            # Utilidades interfaz
│   │   ├── DatabaseUtil.java             # Utilidades BD
│   │   ├── DatabaseTester.java           # Tester BD
│   │   ├── SchemaGenerator.java          # Generador de esquemas
│   │   ├── ConfigChecker.java            # Verificador de configuración
│   │   ├── CrearBibliotecarioInicial.java
│   │   └── FixPasswordHashing.java
│   │
│   ├── ⚠️ exception/                     # Excepciones personalizadas
│   │   ├── BibliotecaException.java      # Excepción base
│   │   ├── BusinessRuleException.java    # Excepción regla negocio
│   │   └── ValidationException.java      # Excepción validación
│   │
│   └── 🔧 persistence/                   # Configuración persistencia
│       └── HibernateUtil.java            # Utilidad Hibernate
│
├── 📁 src/main/webapp/                   # Aplicación web (WAR)
│   ├── index.html, landing.html, spa.html, test-spa.html
│   ├── 📁 css/                           # style.css, spa.css, landing.css
│   ├── 📁 js/                            # main.js, api.js, spa.js, dashboard.js, etc.
│   │   └── core/                         # api-service.js, permission-manager.js
│   └── 📁 WEB-INF/
│       ├── web.xml, sun-jaxws.xml
│       └── jsp/                          # auth/, dashboard/, management/, shared/
│
├── 📁 src/main/resources/                # Configuraciones
│   ├── hibernate-h2.cfg.xml              # Config H2 (desarrollo)
│   ├── hibernate-mysql.cfg.xml           # Config MySQL (producción)
│   ├── hibernate-mysql-team.cfg.xml      # Config MySQL equipo
│   └── logging.properties
│
├── 📁 documentacion/                     # Documentación (este README y más)
│   ├── README.md                         # Este archivo
│   ├── FUNCIONES_IMPLEMENTADAS.md
│   ├── FUNCIONALIDAD_*.md                # Documentación por funcionalidad
│   ├── WEB_SERVICES_SOAP_IMPLEMENTADOS.md
│   ├── REFACTORIZACION_*.md, INSTRUCCIONES_*.md, FIX_*.md
│   └── ...
│
├── 📁 scripts/                           # Scripts de automatización
│   ├── ejecutar-app.sh / .bat            # Ejecutar aplicación (escritorio + web)
│   ├── ejecutar-servidor-integrado.sh    # Solo servidor HTTP
│   └── compile-all.sh, probar-*.sh, ...
│
├── 📁 target/                            # Compilados y WAR
├── 📁 logs/                              # Logs de aplicación
├── 📄 pom.xml                            # Maven (Java 21, packaging WAR)
├── 📄 README.txt                         # Instrucciones rápidas en raíz
└── 📄 .gitignore
📖 Documentación relacionada
    Web y SOAP: WEB_SERVICES_SOAP_IMPLEMENTADOS.md, INICIO_RAPIDO_SOAP.md
    Pruebas web: GUIA_PRUEBAS_WEB.md
    Instrucciones: README.txt en la raíz del proyecto (resumen rápido)
👥 Autores del Proyecto
    🎯 Equipo de Desarrollo
    Roibeth Garcia - GitHub
        
        Desarrollador principal
        Arquitectura del sistema
        Implementación de funcionalidades core
        Refactorización y optimización

    Lucas Machin - GitHub
    
        Desarrollador colaborador
        Implementación de funcionalidades adicionales
        Testing y validación
        Documentación técnica

🤝 Colaboración
Este proyecto es el resultado de la colaboración entre ambos desarrolladores, implementando un sistema completo de gestión de biblioteca comunitaria con todas las funcionalidades requeridas.

🔧 Solución de Problemas
Error: "illegal component position"
✅ SOLUCIONADO: El mensaje de bienvenida ahora se centra correctamente
La aplicación se ejecuta sin problemas
Error de Compilación
Verificar que Java 21+ esté instalado: java -version
Limpiar y recompilar: mvn clean compile
Error de Base de Datos
H2 se crea automáticamente en ./target/h2db/
Verificar permisos de escritura en el directorio
📝 Licencia
Este proyecto es parte del curso PAP (Programación Avanzada y Persistencia).

🎉 ¡Listo para Usar!
La aplicación está completamente funcional en escritorio (Swing) y ofrece además interfaz web (SPA), API REST y servicios SOAP. El mensaje de bienvenida en la interfaz Swing se centra correctamente en cualquier resolución.

📝 Cambios Recientes
✅ Última actualización (documentación)
📚 README: Actualizado para reflejar el estado actual del proyecto.
🛠️ Tecnologías: Java 21, packaging WAR, JAX-WS, Servlets, servidor integrado.
🎯 Ejecución: Documentados los tres modos (escritorio, --server, --soap) y la ruta correcta de scripts (scripts/ejecutar-app.sh).
📁 Estructura: Incluidos paquetes server/, servlet/, webservice/, publisher/, factory/, repositorios y webapp/.
🌐 Web: Descripción de la aplicación web (SPA, API REST, SOAP) y URLs del servidor integrado.
🚀 Funcionalidades del proyecto
✅ Sistema completo de gestión de biblioteca (escritorio)
✅ Interfaz web: login, registro, dashboard por rol, SPA
✅ API REST y servidor HTTP integrado (puerto 8080)
✅ Servicios SOAP (JAX-WS) con WSDL en puertos 9001–9004
✅ Base de datos H2 y MySQL
✅ Documentación y scripts en documentacion/ y scripts/
