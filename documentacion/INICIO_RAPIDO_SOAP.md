# 🚀 Inicio Rápido - Servicios SOAP

## ⚡ Pasos para Iniciar Servicios SOAP

### 1. Compilar el Proyecto
```bash
cd /Users/roibethgarcia/Projects/biblioteca-pap
mvn clean compile
```

### 2. Iniciar Servicios SOAP
```bash
# Opción A: Usando script
./scripts/iniciar-soap.sh

# Opción B: Comando directo
mvn exec:java -Dexec.args="--soap"

# Opción C: Desde MainRefactored
java -cp target/classes edu.udelar.pap.ui.MainRefactored --soap
```

### 3. Verificar que los Servicios Están Activos
```bash
# Opción A: Usando script
./scripts/probar-wsdl.sh

# Opción B: En tu navegador, abre:
open http://localhost:9001/BibliotecarioWS?wsdl
```

## 📋 URLs de los WSDLs

Una vez iniciados los servicios, los WSDLs están disponibles en:

| Servicio | WSDL URL | Puerto |
|----------|----------|--------|
| Bibliotecario | http://localhost:9001/BibliotecarioWS?wsdl | 9001 |
| Lector | http://localhost:9002/LectorWS?wsdl | 9002 |
| Préstamo | http://localhost:9003/PrestamoWS?wsdl | 9003 |
| Donación | http://localhost:9004/DonacionWS?wsdl | 9004 |

## 🧪 Prueba Rápida con cURL

### Ejemplo 1: Obtener cantidad de bibliotecarios
```bash
curl -X POST http://localhost:9001/BibliotecarioWS \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <obtenerCantidadBibliotecarios xmlns="http://webservice.pap.udelar.edu/"/>
  </soap:Body>
</soap:Envelope>'
```

### Ejemplo 2: Obtener estado del servicio
```bash
curl -X POST http://localhost:9001/BibliotecarioWS \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <obtenerEstado xmlns="http://webservice.pap.udelar.edu/"/>
  </soap:Body>
</soap:Envelope>'
```

## 🛠️ Generar Cliente Java desde WSDL

```bash
# 1. Asegurarse que los servicios están corriendo
./scripts/iniciar-soap.sh

# 2. En otra terminal, generar cliente
wsimport -keep -s src/main/java -p edu.udelar.pap.client \
  http://localhost:9001/BibliotecarioWS?wsdl

# 3. Usar el cliente generado en tu código
```

Ejemplo de código cliente:
```java
import edu.udelar.pap.client.*;

public class ClienteEjemplo {
    public static void main(String[] args) {
        // Crear servicio
        BibliotecarioWebService_Service service = 
            new BibliotecarioWebService_Service();
        
        // Obtener puerto
        BibliotecarioWebService port = 
            service.getBibliotecarioWebServicePort();
        
        // Llamar operación
        String resultado = port.obtenerCantidadBibliotecarios();
        System.out.println("Resultado: " + resultado);
    }
}
```

## 🎯 Modos de Ejecución Disponibles

```bash
# Ver ayuda
mvn exec:java -Dexec.args="--help"

# Modo aplicación de escritorio
mvn exec:java

# Modo servidor web HTTP/REST
mvn exec:java -Dexec.args="--server"

# Modo servicios SOAP/WSDL
mvn exec:java -Dexec.args="--soap"
```

## 📚 Operaciones Disponibles

### BibliotecarioWebService
- `crearBibliotecario`
- `obtenerCantidadBibliotecarios`
- `obtenerInfoBibliotecario`
- `verificarEmail`
- `verificarNumeroEmpleado`
- `autenticar`
- `obtenerEstado`

### LectorWebService
- `crearLector`
- `obtenerCantidadLectores`
- `obtenerCantidadLectoresActivos`
- `obtenerInfoLector`
- `verificarEmail`
- `autenticar`
- `cambiarEstadoLector`
- `cambiarZonaLector`

### PrestamoWebService
- `crearPrestamo`
- `obtenerPrestamosPorLector`
- `obtenerPrestamosPorBibliotecario`
- `cambiarEstadoPrestamo`
- `obtenerMaterialesPendientes`

### DonacionWebService
- `registrarDonacionLibro`
- `registrarDonacionArticulo`
- `obtenerDonaciones`
- `sincronizarMateriales`

## 🔧 Solución de Problemas

### Puerto ya en uso
```bash
# Verificar qué proceso está usando el puerto
lsof -i :9001

# Matar el proceso si es necesario
kill -9 <PID>
```

### Servicios no responden
```bash
# Verificar que están compilados
mvn clean compile

# Verificar logs
tail -f logs/biblioteca-pap.log.0
```

### WSDL no se genera
```bash
# Verificar que el plugin está habilitado en pom.xml
grep -A 5 "jaxws-maven-plugin" pom.xml

# Recompilar
mvn clean compile
```

## 📖 Documentación Completa

Para información detallada, consulta:
- `documentacion/WEB_SERVICES_SOAP_IMPLEMENTADOS.md`

## 💡 Consejos

1. **Usa SoapUI** para explorar los servicios visualmente
2. **Los WSDLs** son documentación automática de tus servicios
3. **Todos los servicios** retornan JSON dentro de XML SOAP
4. **No cierra BD**: Los servicios usan la misma base de datos que la app de escritorio
5. **Presiona Ctrl+C** para detener los servicios limpiamente

---

¡Listo para usar! 🎉

