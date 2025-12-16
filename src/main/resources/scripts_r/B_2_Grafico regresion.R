# --- GRAFICO DE REGRESION ---
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) stop("Falta archivo CSV")

archivo_csv <- args[1]
dir_salida <- if (length(args) >= 2) args[2] else "."
ruta_img <- file.path(dir_salida, "grafico_regresion.png")

datos <- read.csv(archivo_csv)
diff_hits <- diff(datos$TLB_Hits)
idx <- which(diff_hits == 0 & datos$Ciclo[-1] > 5)[1]

png(ruta_img, width=800, height=600)
plot(datos$Ciclo, datos$TLB_Misses, main="Regresion: Estabilidad vs Saturacion",
     xlab="Ciclo", ylab="Misses", pch=20, col="gray")

if (!is.na(idx)) {
  ciclo_corte <- datos$Ciclo[idx]
  fase1 <- subset(datos, Ciclo <= ciclo_corte)

  if(nrow(fase1) > 1) {
    clip(min(datos$Ciclo), ciclo_corte, min(datos$TLB_Misses), max(datos$TLB_Misses))
    abline(lm(TLB_Misses ~ Ciclo, data=fase1), col="darkgreen", lwd=3)
  }

  abline(v=ciclo_corte, col="red", lty=2)
  legend("topleft", legend=c("Estable", "Saturacion"), col=c("darkgreen", "red"), lty=c(1, 2), lwd=2)
}
dev.off()
cat(paste("Grafico generado:", ruta_img, "\n"))
