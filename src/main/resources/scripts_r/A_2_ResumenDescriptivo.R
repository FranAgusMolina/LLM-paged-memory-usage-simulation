# --- RESUMEN ESTADISTICO SIMPLE ---
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) stop("Falta archivo CSV")

datos <- read.csv(args[1])
cat("=== ESTADISTICAS CLAVE ===\n")

stats <- function(nombre, col) {
  cat(sprintf("%s -> Min: %d | Max: %d | Prom: %.2f | Desv: %.2f\n",
              nombre, min(col), max(col), mean(col), sd(col)))
}

stats("RAM Ocupada", datos$Marcos_Ocupados)
stats("TLB Misses", datos$TLB_Misses)

# Diagnostico rapido de inestabilidad
cv <- (sd(datos$TLB_Misses) / mean(datos$TLB_Misses)) * 100
if (cv > 50) {
  cat("\nDIAGNOSTICO: Alta inestabilidad (CV > 50%). Posible Thrashing.\n")
} else {
  cat("\nDIAGNOSTICO: Comportamiento estable.\n")
}

# Analisis de carga maxima
total_marcos <- 1024 # Ajusta este valor si tu simulacion usa otro tamano de RAM
uso_max_ram <- max(datos$Marcos_Ocupados)
porcentaje_uso <- (uso_max_ram / total_marcos) * 100

cat(paste("Pico de uso de RAM:", uso_max_ram, "marcos (", round(porcentaje_uso, 1), "% del total teorico de 1024).\n"))