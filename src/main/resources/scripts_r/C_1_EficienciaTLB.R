# --- EFICIENCIA TLB ---
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) stop("Falta archivo CSV")

archivo_csv <- args[1]
dir_salida <- if (length(args) >= 2) args[2] else "."
ruta_img <- file.path(dir_salida, "eficiencia_tlb.png")

datos <- read.csv(archivo_csv)
datos$Total <- datos$TLB_Hits + datos$TLB_Misses
datos$Ratio <- ifelse(datos$Total == 0, 1, datos$TLB_Hits / datos$Total)

cat("=== EFICIENCIA TLB ===\n")
cat(sprintf("Promedio: %.2f%%\n", mean(datos$Ratio, na.rm=TRUE) * 100))
cat(sprintf("Minimo: %.2f%%\n", min(datos$Ratio, na.rm=TRUE) * 100))

png(ruta_img, width=800, height=600)
plot(datos$Ciclo, datos$Ratio, type="l", col="darkgreen", lwd=2,
     main="Eficiencia TLB (Hit Ratio)", xlab="Ciclo", ylab="Hit Ratio (0-1)", ylim=c(0,1))
abline(h=mean(datos$Ratio, na.rm=TRUE), col="red", lty=2)
legend("bottomright", legend=c("Ratio", "Promedio"), col=c("darkgreen", "red"), lty=c(1, 2))
dev.off()
cat(paste("Grafico generado:", ruta_img, "\n"))
