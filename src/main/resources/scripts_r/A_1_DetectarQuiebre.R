# --- DETECCION DE PUNTO DE QUIEBRE Y CORRELACION ---
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) stop("Falta archivo CSV")

datos <- read.csv(args[1])
cat("=== ANALISIS DE QUIEBRE ===\n")

# Detectar donde los Hits dejan de crecer (saturacion)
# Ignoramos los primeros 5 ciclos para evitar falsos positivos al inicio
diff_hits <- diff(datos$TLB_Hits)
# Buscamos el primer momento donde diff es 0 DESPUES del inicio
idx_quiebre <- which(diff_hits == 0 & datos$Ciclo[-1] > 5)[1]

if (!is.na(idx_quiebre)) {
  ciclo <- datos$Ciclo[idx_quiebre]
  cat(paste("PUNTO DE QUIEBRE DETECTADO: Ciclo", ciclo, "\n"))

  # Analisis post-quiebre
  post_data <- subset(datos, Ciclo > ciclo)
  if (nrow(post_data) > 2) {
    correlacion <- cor(post_data$Procesos_Activos, post_data$TLB_Misses)
    cat(paste("Correlacion (Procesos vs Misses):", round(correlacion, 4), "\n"))

    if (abs(correlacion) > 0.9) cat("INTERPRETACION: Colapso directo por sobrecarga de procesos.\n")
    else cat("INTERPRETACION: Degradacion progresiva.\n")
  }
} else {
  cat("SISTEMA ESTABLE: No se detecto punto de quiebre.\n")
}