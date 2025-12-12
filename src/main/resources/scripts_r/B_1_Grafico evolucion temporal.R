# --- 1. CONFIGURACION Y ARGUMENTOS ---
args <- commandArgs(trailingOnly = TRUE)

if (length(args) < 1) {
  stop("Error: No se proporciono la ruta del archivo CSV.")
}

archivo_csv <- args[1]
directorio_salida <- if (length(args) >= 2) args[2] else "."

if (!file.exists(archivo_csv)) {
  stop(paste("Error: El archivo no existe:", archivo_csv))
}

# Construir ruta de salida en el directorio temporal
nombre_imagen <- "evolucion_temporal.png"
ruta_imagen <- file.path(directorio_salida, nombre_imagen)

# --- 2. CARGA DE DATOS ---
datos <- read.csv(archivo_csv, header = TRUE, sep = ",")

# --- 3. CALCULO DEL PUNTO DE QUIEBRE ---
diferencias_hits <- diff(datos$TLB_Hits)
indice_freno <- which(diferencias_hits == 0)[1]
ciclo_saturacion <- NA

if (!is.na(indice_freno)) {
  ciclo_saturacion <- datos$Ciclo[indice_freno]
}

# --- 4. GENERACION DEL GRAFICO ---
# Abrimos el dispositivo PNG (800x600 pixeles)
png(filename = ruta_imagen, width = 800, height = 600)

# Graficamos Ciclo vs Misses
plot(datos$Ciclo, datos$TLB_Misses,
     type = "l",               # "l" para linea continua
     lwd = 3,                  # Grosor de linea
     col = "blue",             # Color azul
     main = "Evolucion Temporal de Fallos de Pagina (TLB Misses)",
     xlab = "Tiempo (Ciclos de Simulacion)",
     ylab = "Acumulado de Fallos (Misses)",
     las = 1)                  # Etiquetas del eje Y horizontales

# Si detectamos saturacion, dibujamos la linea roja
if (!is.na(ciclo_saturacion)) {
  abline(v = ciclo_saturacion, col = "red", lwd = 2, lty = 2) # Linea vertical punteada
  
  # Agregamos texto explicativo
  text(x = ciclo_saturacion, 
       y = max(datos$TLB_Misses) * 0.8, 
       labels = paste("Saturacion\nCiclo", ciclo_saturacion), 
       pos = 4, 
       col = "red")
}

# Agregamos una grilla gris de fondo para que se vea mas profesional
grid(col = "gray", lty = "dotted")

# Cerramos el dispositivo para guardar el archivo
dev.off()

# --- 5. SALIDA PARA JAVA ---
cat(paste(">>> GRAFICO GENERADO: ", basename(ruta_imagen), "\n"))