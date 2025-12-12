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

# --- 2. CARGA DE DATOS ---
datos <- read.csv(archivo_csv, header = TRUE, sep = ",")

# --- 3. FUNCION AUXILIAR DE REPORTE ---
# Funcion para imprimir estadisticas de una columna especifica de forma limpia
reportar_columna <- function(nombre_columna, datos_col) {
  cat(paste("--- Variable:", nombre_columna, "---\n"))
  cat(paste("    Minimo:  ", min(datos_col), "\n"))
  cat(paste("    Maximo:  ", max(datos_col), "\n"))
  cat(paste("    Promedio:", round(mean(datos_col), 2), "\n"))
  cat(paste("    Mediana: ", median(datos_col), "\n"))
  cat(paste("    Desv. Std:", round(sd(datos_col), 2), "\n"))
  cat("\n")
}

# --- 4. GENERACION DEL REPORTE ---
cat("=== RESUMEN ESTADISTICO DESCRIPTIVO ===\n")
cat(paste("Archivo:", basename(archivo_csv), "\n"))
cat(paste("Observaciones (Ciclos):", nrow(datos), "\n\n"))

# Reportamos sobre Marcos Ocupados (Uso de RAM)
reportar_columna("Marcos_Ocupados (RAM)", datos$Marcos_Ocupados)

# Reportamos sobre TLB Misses (Fallos de Pagina)
reportar_columna("TLB_Misses (Fallos)", datos$TLB_Misses)

# --- 5. INTERPRETACION AUTOMATICA ---
cat("=== DIAGNOSTICO RAPIDO ===\n")

# Analisis de variabilidad
cv_misses <- (sd(datos$TLB_Misses) / mean(datos$TLB_Misses)) * 100

if (cv_misses > 50) {
  cat("Alta variabilidad detectada en los Fallos (CV > 50%).\n")
  cat("Esto indica un comportamiento inestable: periodos de calma vs. explosiones de fallos.\n")
} else {
  cat("La variabilidad de fallos es moderada/baja.\n")
}

# Analisis de carga maxima
total_marcos <- 1024 # Ajusta este valor si tu simulacion usa otro tamano de RAM
uso_max_ram <- max(datos$Marcos_Ocupados)
porcentaje_uso <- (uso_max_ram / total_marcos) * 100

cat(paste("Pico de uso de RAM:", uso_max_ram, "marcos (", round(porcentaje_uso, 1), "% del total teorico de 1024).\n"))