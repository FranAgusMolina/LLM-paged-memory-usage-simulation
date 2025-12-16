# --- GRAFICO DE EVOLUCION TEMPORAL ---
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) stop("Falta archivo CSV")

archivo_csv <- args[1]
dir_salida <- if (length(args) >= 2) args[2] else "."
ruta_img <- file.path(dir_salida, "evolucion_temporal.png")

datos <- read.csv(archivo_csv)

# Detectar saturacion
diff_hits <- diff(datos$TLB_Hits)
idx <- which(diff_hits == 0 & datos$Ciclo[-1] > 5)[1]
ciclo_sat <- if (!is.na(idx)) datos$Ciclo[idx] else NA

png(ruta_img, width=800, height=600)
plot(datos$Ciclo, datos$TLB_Misses, type="l", lwd=3, col="blue",
     main="Evolucion de Fallos TLB (Misses)", xlab="Ciclo", ylab="Misses Acumulados")

if (!is.na(ciclo_sat)) {
  abline(v=ciclo_sat, col="red", lwd=2, lty=2)
  text(x=ciclo_sat, y=max(datos$TLB_Misses)*0.8, labels="SATURACION", pos=4, col="red")
}
dev.off()
cat(paste("Grafico generado:", ruta_img, "\n"))
