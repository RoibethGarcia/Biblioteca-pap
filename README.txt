README (abrir en IntelliJ):
1) Open -> /Users/roibethgarcia/Projects/biblioteca-pap
2) Maven import automático.
3) Configura JDK 17.
4) Run: mvn -q -DskipTests exec:java (si instalas Maven) o Shift+F10 en IntelliJ (Main).

Base de datos MySQL:
- Crea la BD: CREATE DATABASE biblioteca_pap CHARACTER SET utf8mb4;
- Ajusta usuario/clave en src/main/resources/hibernate.cfg.xml
