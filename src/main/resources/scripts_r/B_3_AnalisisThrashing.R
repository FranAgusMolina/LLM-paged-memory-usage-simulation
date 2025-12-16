# --- ANALISIS DE THRASHING ---
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) stop("Falta archivo CSV")

archivo_csv <- args[1]
dir_salida <- if (length(args) >= 2) args[2] else "."
ruta_img <- file.path(dir_salida, "analisis_thrashing.png")

datos <- read.csv(archivo_csv)

# Calcular tasa de crecimiento de fallos
tasa_fallos <- c(0, diff(datos$TLB_Misses))
umbral_thrashing <- mean(tasa_fallos) + 2 * sd(tasa_fallos)
idx_thrashing <- which(tasa_fallos > umbral_thrashing)[1]

png(ruta_img, width=800, height=600)
par(mar=c(5,4,4,5)) # Margenes para eje derecho

plot(datos$Ciclo, datos$Procesos_Activos, type="l", col="blue", lwd=2,
     xlab="Ciclo", ylab="Procesos Activos", main="Analisis de Thrashing")

par(new=TRUE)
plot(datos$Ciclo, datos$TLB_Misses, type="l", col="red", lwd=2, axes=FALSE, xlab="", ylab="")
axis(4)
mtext("TLB Misses", side=4, line=3, col="red")

if (!is.na(idx_thrashing)) {
  abline(v=datos$Ciclo[idx_thrashing], col="black", lty=2, lwd=2)
  text(x=datos$Ciclo[idx_thrashing], y=max(datos$TLB_Misses)*0.5, labels="INICIO THRASHING", pos=4)
}

legend("topleft", legend=c("Procesos", "Misses"), col=c("blue", "red"), lty=1, lwd=2)
dev.off()
cat(paste("Grafico generado:", ruta_img, "\n"))
