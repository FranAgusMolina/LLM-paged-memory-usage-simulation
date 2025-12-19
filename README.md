# 🧠 Simulador de Uso de Memoria Paginada para LLMs

Una aplicación de simulación educativa desarrollada en JavaFX que visualiza y demuestra cómo los Modelos de Lenguaje Grande (LLMs) gestionan la memoria mediante el uso de paginación, similar a los sistemas operativos modernos.

## 📋 Descripción

Este simulador implementa un modelo educativo de gestión de memoria para procesos LLM, donde cada proceso representa una conversación activa con un modelo de lenguaje. La aplicación visualiza en tiempo real cómo se asigna y gestiona la memoria física mediante:

- **Paginación de memoria**: División de la memoria en marcos de tamaño fijo
- **Tabla de páginas**: Mapeo de direcciones virtuales a físicas para cada proceso
- **TLB (Translation Lookaside Buffer)**: Caché de traducciones de direcciones con política LRU
- **Asignación dinámica**: Creación y eliminación de procesos LLM durante la simulación
- **Log de accesos**: Registro detallado de cada traducción de dirección virtual a física

## ✨ Características Principales

- 🎨 **Visualización interactiva** de la memoria física en grilla configurable
- 🔄 **Simulación en tiempo real** con controles de play/pause/reinicio
- 📊 **Registro de métricas** (ciclos, procesos activos, uso de memoria, TLB hits/misses)
- 📈 **Análisis estadístico** con scripts de R integrados (detección de thrashing, regresión, correlación)
- 🎯 **Gestión de memoria realista** con algoritmos de paginación
- 🌈 **Código de colores** para identificar diferentes procesos LLM
- ⚙️ **Configuración flexible** mediante perfiles predefinidos
- 📝 **Log de accesos a memoria** con información detallada de cada traducción
- 🔧 **Ventana de configuración** para ajustar parámetros de simulación

## 🛠️ Tecnologías Utilizadas

- **Java 17** - Lenguaje de programación principal
- **JavaFX 17.0.6** - Framework para la interfaz gráfica
- **Maven** - Gestión de dependencias y construcción
- **R** - Análisis estadístico y visualización de datos
- **JUnit 5** - Testing unitario

## 📦 Requisitos Previos

Antes de ejecutar el simulador, asegúrate de tener instalado:

- Java Development Kit (JDK) 17 o superior
- Apache Maven 3.6+
- (Opcional) R 4.0+ para ejecutar los scripts de análisis

## 🚀 Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/FranAgusMolina/LLM-paged-memory-usage-simulation.git
   cd LLM-paged-memory-usage-simulation
   ```

2. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación:**
   ```bash
   mvn javafx:run
   ```

## 💻 Uso

### Interfaz Principal

Al iniciar la aplicación, verás:

- **Grilla de memoria**: Representación visual de los marcos de memoria física (configurable por perfil)
- **Panel de control**: Botones para iniciar, pausar, reiniciar y generar reportes
- **Lista de procesos**: Procesos LLM activos con ID, nombre, tokens y marcos asignados
- **Tabla de páginas**: Mapeo de páginas virtuales a marcos físicos del proceso seleccionado
- **Contenido TLB**: Visualización de las entradas en la caché TLB con PID, página y marco
- **Log de accesos**: Registro de traducciones de direcciones con dirección virtual, física, página, marco y resultado TLB
- **Métricas en tiempo real**: TLB hits, misses y tasa de aciertos

### Controles de Simulación

- **▶ Iniciar**: Comienza la simulación con creación aleatoria de procesos
- **⏸ Pausar**: Detiene temporalmente la simulación (mantiene el estado)
- **↺ Reiniciar**: Reinicia completamente la simulación limpiando todos los recursos
- **📊 Generar Reporte**: Ejecuta scripts de R para análisis estadístico
- **⚙ Configuración**: Abre ventana para seleccionar perfil y ajustar parámetros

### Perfiles de Configuración

La aplicación incluye perfiles predefinidos en `src/main/resources/perfiles.txt`:

- **Servidor Estándar (ChatGPT)**: Configuración para simular ChatGPT en producción
- **Servidor Pequeño**: Configuración para sistemas con recursos limitados
- **Laptop de Desarrollo**: Configuración para entornos de desarrollo local

Cada perfil define:
- Tamaño de la grilla (columnas × filas)
- Tamaño de página (tokens por marco)
- Tamaño de TLB (número de entradas)
- Velocidad de simulación (ms por ciclo)

## 📁 Estructura del Proyecto

```
src/main/java/sim/
├── Aplicacion/
│   └── App.java                    # Punto de entrada de la aplicación
├── controlador/
│   ├── CordinadorApp.java          # Coordinador principal MVC
│   ├── ControladorUI.java          # Controlador de la interfaz principal
│   ├── ControladorConfig.java      # Controlador de configuración
│   ├── ReportController.java       # Gestión de reportes estadísticos
│   └── ReportViewer.java           # Visualización de gráficos generados
├── modelo/
│   ├── Frame.java                  # Marco de memoria física
│   ├── LLMProcess.java             # Proceso LLM individual
│   ├── PageTable.java              # Tabla de páginas
│   ├── PhysicalMemory.java         # Memoria física completa
│   ├── Perfil.java                 # Perfil de configuración
│   └── MemoryAccessLog.java        # Registro de acceso a memoria
├── negocio/
│   ├── MMUService.java             # Unidad de Gestión de Memoria
│   ├── SimulationManager.java      # Gestor de la simulación
│   └── TLB.java                    # Translation Lookaside Buffer (LRU)
├── recorder/
│   ├── Auditador.java              # Registro de métricas en CSV
│   └── RScriptRunner.java          # Ejecución de scripts R
├── datos/
│   ├── CargarPerfiles.java         # Cargador de perfiles desde archivo
│   └── Constantes.java             # Constantes de visualización
├── UI/
│   ├── DialogManager.java          # Gestión de diálogos y alertas
│   └── MemoryGrid.java             # Grilla visual de memoria
└── test/
    └── MMUServiceTest.java         # Tests unitarios

src/main/resources/
├── visualizacion.fxml              # Interfaz principal
├── configuracion.fxml              # Ventana de configuración
├── perfiles.txt                    # Definición de perfiles
├── scripts_r/                      # Scripts de análisis estadístico
│   ├── A_1_DetectarQuiebre.R       # Detección de punto de saturación
│   ├── A_2_ResumenDescriptivo.R    # Estadísticas descriptivas
│   ├── B_1_Grafico evolucion temporal.R  # Evolución temporal
│   ├── B_2_Grafico regresion.R     # Regresión lineal
│   ├── B_3_AnalisisThrashing.R     # Análisis de thrashing
│   └── C_1_EficienciaTLB.R         # Eficiencia de la TLB
└── datos/                          # Archivos CSV generados (temporal)
```

## 🔍 Componentes Clave

### CordinadorApp
Coordinador principal que implementa el patrón MVC:
- Inicializa todos los componentes de la aplicación
- Conecta la lógica de negocio con la interfaz gráfica
- Gestiona el ciclo de vida de la aplicación
- Sincroniza el estado de la simulación con la UI
- Maneja la configuración y reinicio con diferentes perfiles

### SimulationManager
Orquesta el ciclo de vida completo de la simulación:
- Creación aleatoria de procesos LLM (30% probabilidad por ciclo)
- Generación de tokens y asignación de memoria
- Registro de métricas en cada ciclo mediante el Auditador
- Control de ejecución (iniciar/pausar/detener/reiniciar)
- Callbacks para actualización de UI y registro de accesos

### MMUService (Memory Management Unit)
Implementa los algoritmos de gestión de memoria:
- Traducción de direcciones virtuales a físicas
- Gestión de la TLB con política LRU para acelerar traducciones
- Asignación y liberación de marcos de memoria
- Manejo de tablas de páginas por proceso
- Generación de logs detallados de cada acceso a memoria

### TLB (Translation Lookaside Buffer)
Implementa una caché de traducciones con política LRU:
- LinkedHashMap con orden de acceso para implementar LRU eficientemente
- Invalidación selectiva por proceso
- Métricas de hits y misses
- Capacidad configurable por perfil

### LLMProcess
Representa un proceso de conversación con el LLM:
- Identificador único (PID)
- Tabla de páginas propia (mapeo página virtual → marco físico)
- Contador de tokens generados
- Color de identificación en la UI

### PhysicalMemory
Modela la memoria física del sistema:
- Array de marcos de tamaño fijo
- Seguimiento de ocupación por proceso
- Asignación y liberación de marcos
- Cálculo de estadísticas (marcos libres/ocupados)

### Auditador
Sistema de registro de métricas:
- Genera archivos CSV temporales únicos
- Registra ciclo, procesos activos, marcos ocupados, TLB hits/misses
- Limpieza automática al cerrar la aplicación
- Gestión de múltiples instancias simultáneas

### MemoryAccessLog
Registro detallado de cada acceso a memoria:
- Dirección virtual solicitada (índice del token)
- Dirección física calculada (marco × tamaño_página + offset)
- Número de página virtual y marco físico
- Resultado TLB (HIT o MISS)
- Información del proceso (PID y nombre)

## 📊 Análisis de Datos

La aplicación genera automáticamente archivos CSV temporales con métricas de cada ciclo:

- Número de ciclo
- Procesos activos
- Marcos de memoria ocupados
- TLB hits acumulados
- TLB misses acumulados

### Scripts de R Disponibles

El simulador incluye varios scripts de análisis estadístico:

1. **A_1_DetectarQuiebre.R**: Detecta el punto de saturación de memoria
2. **A_2_ResumenDescriptivo.R**: Genera estadísticas descriptivas y diagnóstico de thrashing
3. **B_1_Grafico evolucion temporal.R**: Visualiza la evolución temporal de métricas
4. **B_2_Grafico regresion.R**: Análisis de regresión lineal y correlaciones
5. **B_3_AnalisisThrashing.R**: Detección y análisis de fenómenos de thrashing
6. **C_1_EficienciaTLB.R**: Análisis de eficiencia de la TLB

Para ejecutar un script:
1. Ejecuta una simulación completa
2. Haz clic en el botón **📊 Generar Reporte**
3. Selecciona el script deseado
4. El sistema generará gráficos PNG en `src/main/resources/img/temp/`

**Nota**: Los archivos temporales (CSV e imágenes) se eliminan automáticamente al cerrar la aplicación.

## 🧪 Testing

Ejecutar las pruebas unitarias:
```bash
mvn test
```

## 🎓 Propósito Educativo

Este proyecto tiene fines educativos y demuestra:

1. **Gestión de memoria virtual** con paginación
2. **Algoritmos de traducción de direcciones**
3. **Funcionamiento de la TLB** y su impacto en el rendimiento
4. **Concurrencia** con múltiples procesos
5. **Visualización de conceptos abstractos** de sistemas operativos
6. **Simulación de sistemas** complejos

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu característica (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es de código abierto y está disponible para uso educativo.

## 👤 Autor

**Francisco Agustín Molina**

- GitHub: [@FranAgusMolina](https://github.com/FranAgusMolina)

## 🙏 Agradecimientos

- Inspirado en los conceptos de sistemas operativos y arquitectura de computadoras
- Diseñado para ayudar a comprender cómo los LLMs gestionan el contexto en memoria

---

⭐ Si este proyecto te resulta útil, considera darle una estrella en GitHub!
