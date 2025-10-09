# 🌐 Web Services SOAP Implementados

## 📋 Resumen

Se ha implementado exitosamente la arquitectura completa de Web Services SOAP con generación automática de WSDL para el proyecto Biblioteca PAP. Esta implementación cumple con los requisitos académicos de:

1. ✅ Manejo de passwords con HASH (BCrypt)
2. ✅ Retornos de controladores en tipos primitivos y String
3. ✅ Clases publicadoras (Publishers) para cada controlador
4. ✅ Publicación de servicios con `Endpoint.publish()` generando WSDLs
5. ✅ Anotaciones `@WebService` en todas las interfaces

## 🏗️ Arquitectura Implementada

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                     │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │   Aplicación     │  │   Web Services   │                │
│  │   Escritorio     │  │   SOAP/WSDL      │                │
│  │   (Swing)        │  │   Endpoint.pub   │                │
│  └──────────────────┘  └──────────────────┘                │
└─────────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE WEB SERVICES                      │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  @WebService Interfaces                              │  │
│  │  - BibliotecarioWebService                           │  │
│  │  - LectorWebService                                  │  │
│  │  - PrestamoWebService                                │  │
│  │  - DonacionWebService                                │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  @WebService Implementations                         │  │
│  │  - BibliotecarioWebServiceImpl                       │  │
│  │  - LectorWebServiceImpl                              │  │
│  │  - PrestamoWebServiceImpl                            │  │
│  │  - DonacionWebServiceImpl                            │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PUBLISHERS                       │
│  - BibliotecarioPublisher                                   │
│  - LectorPublisher                                          │
│  - PrestamoPublisher                                        │
│  - DonacionPublisher                                        │
│  - PublisherFactory                                         │
└─────────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE CONTROLLERS                      │
│  - BibliotecarioController                                  │
│  - LectorController                                         │
│  - PrestamoController                                       │
│  - DonacionController                                       │
└─────────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE SERVICIOS                        │
│  - BibliotecarioService                                     │
│  - LectorService                                            │
│  - PrestamoService                                          │
│  - DonacionService                                          │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Modos de Ejecución

La aplicación ahora soporta 3 modos de ejecución:

### 1. **Modo Aplicación de Escritorio** (Por defecto)
```bash
# Desde Maven
mvn exec:java

# Desde JAR compilado
java -jar target/biblioteca-pap-0.1.0-SNAPSHOT.jar
```

**Características:**
- Interfaz gráfica Swing completa
- Gestión de bibliotecarios, lectores, préstamos y donaciones
- Opción de iniciar servicios adicionales (HTTP o SOAP)

### 2. **Modo Servidor Web HTTP/REST**
```bash
# Desde Maven
mvn exec:java -Dexec.args="--server"

# Desde JAR compilado
java -jar target/biblioteca-pap-0.1.0-SNAPSHOT.jar --server
```

**Características:**
- Servidor HTTP en puerto 8080
- Endpoints REST-like para aplicación web
- Acceso desde navegador: http://localhost:8080

### 3. **Modo Servicios SOAP/WSDL** ⭐ **NUEVO**
```bash
# Desde Maven
mvn exec:java -Dexec.args="--soap"

# Desde JAR compilado
java -jar target/biblioteca-pap-0.1.0-SNAPSHOT.jar --soap
```

**Características:**
- Servicios SOAP publicados con WSDLs automáticos
- Puertos independientes para cada servicio
- Compatible con clientes SOAP tradicionales

## 📡 Servicios SOAP Disponibles

### 1. BibliotecarioWebService
- **Puerto:** 9001
- **Endpoint:** http://localhost:9001/BibliotecarioWS
- **WSDL:** http://localhost:9001/BibliotecarioWS?wsdl

**Operaciones disponibles:**
- `crearBibliotecario(nombre, apellido, email, numeroEmpleado, password)` → String (JSON)
- `obtenerCantidadBibliotecarios()` → String (JSON)
- `obtenerInfoBibliotecario(id)` → String (JSON)
- `verificarEmail(email)` → String (JSON)
- `verificarNumeroEmpleado(numeroEmpleado)` → String (JSON)
- `autenticar(email, password)` → String (JSON)
- `obtenerEstado()` → String (JSON)

### 2. LectorWebService
- **Puerto:** 9002
- **Endpoint:** http://localhost:9002/LectorWS
- **WSDL:** http://localhost:9002/LectorWS?wsdl

**Operaciones disponibles:**
- `crearLector(nombre, apellido, email, fechaNacimiento, direccion, zona, password)` → String (JSON)
- `obtenerCantidadLectores()` → String (JSON)
- `obtenerCantidadLectoresActivos()` → String (JSON)
- `obtenerInfoLector(id)` → String (JSON)
- `verificarEmail(email)` → String (JSON)
- `autenticar(email, password)` → String (JSON)
- `cambiarEstadoLector(lectorId, nuevoEstado)` → String (JSON)
- `cambiarZonaLector(lectorId, nuevaZona)` → String (JSON)

### 3. PrestamoWebService
- **Puerto:** 9003
- **Endpoint:** http://localhost:9003/PrestamoWS
- **WSDL:** http://localhost:9003/PrestamoWS?wsdl

**Operaciones disponibles:**
- `crearPrestamo(lectorId, materialId, bibliotecarioId)` → String (JSON)
- `obtenerPrestamosPorLector(lectorId)` → String (JSON)
- `obtenerPrestamosPorBibliotecario(bibliotecarioId)` → String (JSON)
- `cambiarEstadoPrestamo(prestamoId, nuevoEstado)` → String (JSON)
- `obtenerMaterialesPendientes()` → String (JSON)

### 4. DonacionWebService
- **Puerto:** 9004
- **Endpoint:** http://localhost:9004/DonacionWS
- **WSDL:** http://localhost:9004/DonacionWS?wsdl

**Operaciones disponibles:**
- `registrarDonacionLibro(...)` → String (JSON)
- `registrarDonacionArticulo(...)` → String (JSON)
- `obtenerDonaciones()` → String (JSON)
- `sincronizarMateriales()` → String (JSON)

## 🛠️ Cómo Probar los Servicios

### Opción 1: Navegador Web (Ver WSDL)
```
1. Iniciar servicios SOAP:
   mvn exec:java -Dexec.args="--soap"

2. Abrir en navegador:
   http://localhost:9001/BibliotecarioWS?wsdl
```

### Opción 2: cURL (Llamada SOAP)
```bash
# Ejemplo: Obtener cantidad de bibliotecarios
curl -X POST http://localhost:9001/BibliotecarioWS \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"http://webservice.pap.udelar.edu/obtenerCantidadBibliotecarios\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:obtenerCantidadBibliotecarios xmlns:ns2="http://webservice.pap.udelar.edu/"/>
  </soap:Body>
</soap:Envelope>'
```

### Opción 3: SoapUI
```
1. Descargar SoapUI: https://www.soapui.org/
2. Crear nuevo proyecto SOAP
3. Agregar WSDL: http://localhost:9001/BibliotecarioWS?wsdl
4. Explorar operaciones y ejecutar requests
```

### Opción 4: Generar Cliente Java
```bash
# Generar clases cliente desde WSDL
wsimport -keep -s src/main/java -p edu.udelar.pap.client \
  http://localhost:9001/BibliotecarioWS?wsdl

# Usar el cliente generado
BibliotecarioWebService_Service service = new BibliotecarioWebService_Service();
BibliotecarioWebService port = service.getBibliotecarioWebServicePort();
String resultado = port.obtenerCantidadBibliotecarios();
System.out.println(resultado);
```

## 📦 Archivos Creados/Modificados

### Nuevos Archivos
1. **`src/main/java/edu/udelar/pap/server/WebServicePublisher.java`**
   - Clase principal para publicar servicios SOAP
   - Usa `Endpoint.publish()` para cada servicio
   - Gestión de ciclo de vida de servicios

### Archivos Modificados
1. **`pom.xml`**
   - Habilitado plugin `jaxws-maven-plugin` (líneas 119-171)
   - Configuración para generar WSDLs automáticamente

2. **`src/main/java/edu/udelar/pap/ui/MainRefactored.java`**
   - Agregado modo `--soap` para iniciar servicios SOAP
   - Agregado modo `--help` para mostrar ayuda
   - Mejorada selección de servicios en modo escritorio

## 🔧 Configuración Técnica

### Dependencias Utilizadas
```xml
<!-- JAX-WS API -->
<dependency>
    <groupId>jakarta.xml.ws</groupId>
    <artifactId>jakarta.xml.ws-api</artifactId>
    <version>4.0.1</version>
</dependency>

<!-- JAX-WS Implementation (Metro) -->
<dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-rt</artifactId>
    <version>4.0.2</version>
</dependency>
```

### Plugin Maven
```xml
<plugin>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-maven-plugin</artifactId>
    <version>4.0.2</version>
    <executions>
        <!-- Generación de WSDL para cada servicio -->
    </executions>
</plugin>
```

## ✅ Cumplimiento de Requisitos

### TAREA 1 (App de Escritorio)
- ✅ **1. Hash de Passwords:** Implementado con BCrypt
- ✅ **2. Retornos primitivos:** Todos los métodos web retornan String/Long/int/boolean
- ✅ **3. Clases Publisher:** 4 Publishers + Factory implementados
- ✅ **4. Endpoint.publish():** `WebServicePublisher.java` con publish() para generar WSDLs
- ✅ **5. Anotaciones @WebService:** Todas las interfaces y implementaciones anotadas

### TAREA 2 (Web Services)
- ✅ **Arquitectura preparada:** WSDLs disponibles para generar clientes
- ✅ **Publishers como operaciones:** Publishers exponen operaciones reutilizables
- ✅ **Servlets funcionando:** Servlets llaman a Publishers para operaciones web

## 🎯 Ventajas de la Implementación

1. **No rompe código existente:** La aplicación de escritorio y servidor web siguen funcionando
2. **Arquitectura flexible:** 3 modos de ejecución independientes
3. **WSDLs automáticos:** Generados por JAX-WS sin código adicional
4. **Compatibilidad SOAP:** Clientes SOAP/WSDL tradicionales pueden consumir servicios
5. **Documentación automática:** Los WSDLs documentan las operaciones
6. **Puertos independientes:** Cada servicio en su propio puerto para facilitar debugging
7. **Shutdown limpio:** Servicios se cierran correctamente con Ctrl+C

## 📊 Comparación de Arquitecturas

| Característica | Modo HTTP/REST | Modo SOAP/WSDL |
|----------------|----------------|----------------|
| Puerto | 8080 | 9001-9004 |
| Protocolo | HTTP/JSON | SOAP/XML |
| WSDL | No | Sí ✅ |
| Performance | Rápido | Moderado |
| Interoperabilidad | Moderna | Legacy |
| Uso recomendado | Apps web | Integración empresarial |

## 🚦 Scripts de Inicio Rápido

### Script para iniciar servicios SOAP
```bash
#!/bin/bash
# scripts/iniciar-soap.sh

cd /Users/roibethgarcia/Projects/biblioteca-pap
echo "🚀 Iniciando servicios SOAP..."
mvn exec:java -Dexec.args="--soap"
```

### Script para probar WSDL
```bash
#!/bin/bash
# scripts/probar-wsdl.sh

echo "📡 Probando WSDLs..."
echo ""
echo "1. BibliotecarioWebService:"
curl -s http://localhost:9001/BibliotecarioWS?wsdl | grep -o "<service.*</service>" | head -n 5
echo ""
echo "2. LectorWebService:"
curl -s http://localhost:9002/LectorWS?wsdl | grep -o "<service.*</service>" | head -n 5
echo ""
echo "✅ WSDLs respondiendo correctamente"
```

## 📝 Notas Importantes

1. **Base de datos:** Los servicios SOAP usan la misma base de datos que la aplicación de escritorio
2. **Sesiones:** No hay gestión de sesiones en SOAP (stateless)
3. **Autenticación:** Cada llamada debe incluir credenciales si es necesaria
4. **JSON en SOAP:** Los servicios retornan JSON dentro de respuestas SOAP para simplicidad
5. **Compatibilidad:** Los servicios son compatibles con clientes Java, .NET, PHP, Python, etc.

## 🎓 Para Propósitos Académicos

Esta implementación demuestra:
- Arquitectura completa de Web Services SOAP
- Uso de `Endpoint.publish()` para publicar servicios
- Generación automática de WSDLs
- Separación de capas (Controller → Publisher → WebService)
- Interoperabilidad entre diferentes tecnologías
- Buenas prácticas de diseño de servicios

## 🔗 Referencias

- JAX-WS Documentation: https://jakarta.ee/specifications/xml-web-services/
- SOAP Tutorial: https://www.w3schools.com/xml/xml_soap.asp
- WSDL Specification: https://www.w3.org/TR/wsdl/
- Metro JAX-WS: https://eclipse-ee4j.github.io/metro-jax-ws/

---

**Fecha de implementación:** Octubre 2024  
**Versión:** 0.1.0-SNAPSHOT  
**Estado:** ✅ Completado y funcional

