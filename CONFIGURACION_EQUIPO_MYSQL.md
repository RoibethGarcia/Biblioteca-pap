# Configuración de Base de Datos MySQL para el Equipo

## Información de Conexión

- **Host**: 192.168.1.5
- **Puerto**: 3306
- **Base de datos**: BD_Pap
- **Usuario**: biblioteca_user
- **Contraseña**: biblioteca_pass

## Configuración para cada miembro del equipo

### Paso 1: Crear archivo de configuración local

Cada miembro del equipo debe crear un archivo de configuración local:

```bash
# Crear directorio de configuración
mkdir -p ~/.biblioteca-pap

# Crear archivo de configuración
cat > ~/.biblioteca-pap/database.properties << EOF
# Configuración de base de datos para desarrollo en equipo
db.host=192.168.1.5
db.port=3306
db.name=BD_Pap
db.user=biblioteca_user
db.password=biblioteca_pass
EOF
```

### Paso 2: Actualizar configuración de Hibernate

Cada miembro debe crear su propio archivo de configuración:

```xml
<!-- Crear archivo: src/main/resources/hibernate-mysql-team.cfg.xml -->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://192.168.1.5:3306/BD_Pap?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC</property>
        <property name="hibernate.connection.username">biblioteca_user</property>
        <property name="hibernate.connection.password">biblioteca_pass</property>

        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <mapping class="edu.udelar.pap.domain.Usuario"/>
        <mapping class="edu.udelar.pap.domain.Lector"/>
        <mapping class="edu.udelar.pap.domain.Bibliotecario"/>
        <mapping class="edu.udelar.pap.domain.DonacionMaterial"/>
        <mapping class="edu.udelar.pap.domain.Libro"/>
        <mapping class="edu.udelar.pap.domain.ArticuloEspecial"/>
        <mapping class="edu.udelar.pap.domain.Prestamo"/>
    </session-factory>
</hibernate-configuration>
```

### Paso 3: Actualizar HibernateUtil.java

Agregar soporte para la configuración del equipo:

```java
// En HibernateUtil.java, agregar este caso:
case "mysql-team":
    cfg = "hibernate-mysql-team.cfg.xml";
    break;
```

### Paso 4: Ejecutar la aplicación

```bash
# Para desarrollo en equipo
mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.Main" -Ddb=mysql-team

# Para desarrollo local (H2)
mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.Main" -Ddb=h2
```

## Verificación de Conexión

### Desde línea de comandos:
```bash
mysql -h 192.168.1.5 -u biblioteca_user -p BD_Pap
```

### Desde la aplicación:
```bash
mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.Main" -Ddb=mysql-team
```

## Solución de Problemas

### Error de conexión:
```
Error: No se pudo conectar con la base de datos
```

**Posibles causas:**
1. **Firewall**: Verificar que el puerto 3306 está abierto
2. **Red**: Verificar que están en la misma red WiFi
3. **MySQL**: Verificar que MySQL está ejecutándose

**Solución:**
```bash
# Verificar si MySQL está ejecutándose en el servidor
brew services list | grep mysql

# Verificar conectividad
ping 192.168.1.5

# Verificar puerto
telnet 192.168.1.5 3306
```

### Error de permisos:
```
Access denied for user 'biblioteca_user'@'192.168.1.X'
```

**Solución:**
```sql
-- Ejecutar en el servidor MySQL
GRANT ALL PRIVILEGES ON BD_Pap.* TO 'biblioteca_user'@'%';
FLUSH PRIVILEGES;
```

## Comandos Útiles

### En el servidor (tu máquina):
```bash
# Verificar estado de MySQL
brew services list | grep mysql

# Reiniciar MySQL
brew services restart mysql

# Ver logs de MySQL
tail -f /opt/homebrew/var/log/mysql/error.log
```

### En las máquinas del equipo:
```bash
# Probar conexión
mysql -h 192.168.1.5 -u biblioteca_user -p BD_Pap

# Ejecutar aplicación
mvn exec:java -Dexec.mainClass="edu.udelar.pap.ui.Main" -Ddb=mysql-team
```

## Notas Importantes

1. **Siempre usar `-Ddb=mysql-team`** para desarrollo en equipo
2. **Usar `-Ddb=h2`** para desarrollo local individual
3. **Verificar conexión** antes de empezar a trabajar
4. **Hacer backups** regularmente de la base de datos
5. **Comunicar cambios** importantes al equipo

## Backup y Restore

### Crear backup:
```bash
mysqldump -u biblioteca_user -p BD_Pap > backup_$(date +%Y%m%d_%H%M%S).sql
```

### Restaurar backup:
```bash
mysql -u biblioteca_user -p BD_Pap < backup_20250118_143022.sql
```
