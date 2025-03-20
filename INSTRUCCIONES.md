# Sistema de Gestión de Turnos

Este documento proporciona instrucciones para compilar, ejecutar y utilizar el Sistema de Gestión de Turnos desarrollado para el curso de Programación Orientada a Objetos.

## Estructura del Proyecto

La estructura principal del proyecto es la siguiente:

```
proyecto/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── queuemanagementsystem/
│                   ├── model/            # Clases de modelo (Usuario, Cliente, Ticket, etc.)
│                   ├── repository/       # Repositorios para persistencia de datos
│                   ├── service/          # Servicios de lógica de negocio
│                   ├── ui/               # Interfaces de usuario
│                   ├── util/             # Clases utilitarias
│                   └── Main.java         # Clase principal
├── data/                                 # Almacenamiento de archivos JSON
│   ├── users.json
│   ├── clients.json
│   ├── tickets.json
│   ├── stations.json
│   └── categories.json
└── INSTRUCCIONES.md                      # Este archivo
```

## Requisitos Previos

- Java Development Kit (JDK) 11 o superior
- Recomendado: IDE como IntelliJ IDEA, Eclipse o NetBeans

## Compilación y Ejecución

### Usando un IDE

1. Abrir el proyecto en su IDE preferido
2. Configurar el JDK (11 o superior)
3. Establecer `src/main/java` como directorio de código fuente
4. Ejecutar la clase `com.queuemanagementsystem.Main`

### Compilación Manual

1. Abrir una terminal y navegar a la carpeta raíz del proyecto
2. Crear la carpeta para los archivos compilados:
   ```
   mkdir -p out/production
   ```
3. Compilar el código fuente:
   ```
   javac -d out/production -cp ".:lib/*" src/main/java/com/queuemanagementsystem/Main.java src/main/java/com/queuemanagementsystem/*/*.java
   ```
4. Ejecutar la aplicación:
   ```
   java -cp "out/production:lib/*" com.queuemanagementsystem.Main
   ```

## Flujo del Programa

Al iniciar el programa, se mostrará un menú principal con las siguientes opciones:

1. **Cliente** - Para solicitar turnos y verificar el estado de la cola
2. **Empleado** - Para atender turnos y gestionar la atención al cliente
3. **Administrador** - Para configurar categorías, estaciones y gestionar estadísticas
4. **Salir** - Para finalizar el programa

### Funcionamiento Como Cliente

El cliente puede:
- Registrarse o ingresar con un ID existente
- Solicitar un nuevo turno seleccionando una categoría
- Verificar el estado de la cola
- Ver sus tickets activos e históricos
- Cancelar tickets en espera

### Funcionamiento Como Empleado

El empleado puede:
- Obtener el siguiente cliente de la cola
- Completar el servicio actual
- Ver información del cliente que está atendiendo
- Pausar/reanudar la asignación de turnos
- Ver un resumen de su actividad diaria

### Funcionamiento Como Administrador

El administrador puede:
- Gestionar categorías (crear, activar/desactivar, actualizar)
- Gestionar estaciones (crear, abrir/cerrar, asignar empleados)
- Gestionar empleados (registrar, asignar a categorías)
- Ver estadísticas del sistema
- Generar informes de productividad

## Credenciales de Acceso

### Administrador
- **ID**: admin
- **Contraseña**: admin123

### Empleados
- **ID**: emp1
- **Contraseña**: pass123

- **ID**: emp2
- **Contraseña**: pass123

- **ID**: emp3
- **Contraseña**: pass123

### Clientes
Los clientes pueden crear una nueva cuenta o utilizar alguna de las cuentas existentes:
- C001 (Sofia Alejandra Martínez Rivas)
- C002 (José Antonio Hernández Portillo)
- C003 (Gabriela María Flores Melara)
- etc.

## Persistencia de Datos

El sistema utiliza archivos JSON para almacenar los datos. Estos archivos se crean automáticamente en la carpeta `data/` cuando se ejecuta el programa por primera vez. Si los archivos no existen, el sistema inicializa datos de ejemplo.

## Notas Importantes

1. El sistema está diseñado para ejecutarse en modo consola (CLI).
2. Al iniciar por primera vez, se crean datos de muestra si no existen previamente.
3. Los cambios realizados se guardan automáticamente en los archivos JSON.
4. Si ocurren problemas con la persistencia, verificar que la carpeta `data/` exista y tenga permisos de escritura.

## Resolución de Problemas

### Error: "No se puede encontrar o cargar la clase principal"
- Verificar que la estructura de directorios sea correcta
- Asegurarse de que se está ejecutando desde el directorio raíz del proyecto

### Error: "Exception in thread main"
- Verificar que las dependencias estén correctamente incluidas en el classpath
- Comprobar que la carpeta `data/` exista y tenga permisos de escritura

### No se pueden guardar cambios
- Verificar que el usuario tenga permisos de escritura para la carpeta `data/`
- Comprobar que no haya otro proceso utilizando los archivos JSON