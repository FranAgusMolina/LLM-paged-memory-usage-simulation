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
# Importante: sep="," porque tu archivo data(1).csv usa comas
datos <- read.csv(archivo_csv, header = TRUE, sep = ",")

# --- 3. REPORTE DE TEXTO ---
cat("=== REPORTE DE ANALISIS ESTADISTICO ===\n")
cat(paste("Archivo:", basename(archivo_csv), "\n"))
cat(paste("Total de ciclos simulados:", nrow(datos), "\n\n"))

# --- 4. DETECCION DE SATURACION (Punto de Quiebre) ---
# Buscamos el momento exacto donde los Hits dejan de aumentar
# diff() calcula la diferencia entre un valor y el siguiente
# Si la diferencia es 0, significa que no hubo nuevos hits
diferencias_hits <- diff(datos$TLB_Hits)

# Buscamos el primer indice donde la diferencia sea 0
# Sumamos 1 al indice porque diff reduce el vector en 1 elemento
indice_freno <- which(diferencias_hits == 0)[1] 

if (!is.na(indice_freno)) {
  # El ciclo real suele ser el indice + el ciclo inicial (ajuste segun tus datos)
  ciclo_saturacion <- datos$Ciclo[indice_freno]
  
  cat(paste(">>> PUNTO DE SATURACION DETECTADO: Ciclo", ciclo_saturacion, "\n"))
  cat("    A partir de este ciclo, la memoria dejo de responder eficientemente.\n\n")
  
  # --- 5. ANALISIS DE CORRELACION (Fase Critica) ---
  # Filtramos solo los datos despues del desastre
  fase_critica <- subset(datos, Ciclo > ciclo_saturacion)
  
  # Solo calculamos si hay suficientes datos (mas de 2 puntos)
  if (nrow(fase_critica) > 2) {
    cat(">>> ANALISIS DE CORRELACION (Fase de Saturacion)\n")
    
    # Correlacion entre cantidad de procesos y cantidad de fallos (Misses)
    correlacion <- cor(fase_critica$Procesos_Activos, fase_critica$TLB_Misses)
    r_valor <- round(correlacion, 4)
    
    cat(paste("    Coeficiente de Pearson (r):", r_valor, "\n"))
    cat("    INTERPRETACION: ")
    
    if (abs(correlacion) > 0.9) {
      cat("Relacion POSITIVA MUY FUERTE.\n")
      cat("    Conclusion: Agregar mas procesos causa directamente el colapso de memoria.\n")
    } else if (abs(correlacion) > 0.7) {
      cat("Relacion POSITIVA FUERTE.\n")
    } else {
      cat("Relacion moderada o debil.\n")
    }
    
  } else {
    cat(">>> No hay suficientes datos post-saturacion para calcular correlacion.\n")
  }
  
} else {
  cat(">>> NO SE DETECTO SATURACION.\n")
  cat("    El sistema se mantuvo estable durante toda la simulacion.\n")
}