#!/usr/bin/env Rscript
# Opción 4: Análisis de Punto de Saturación

args <- commandArgs(trailingOnly = TRUE)
if (length(args) == 0) {
  stop("Error: No se proporcionó archivo CSV")
}

archivo_csv <- args[1]
datos <- read.csv(archivo_csv)

cat("=== REPORTE DE ANÁLISIS ESTADÍSTICO ===\n")
cat("Archivo analizado:", basename(archivo_csv), "\n")
cat("Total de ciclos simulados:", nrow(datos), "\n\n")

# Detectar punto de saturación
diferencias_hits <- diff(datos$TLB_Hits)
punto_saturacion <- which(diferencias_hits == 0)[1]

if (! is.na(punto_saturacion)) {
  cat(">>> PUNTO DE SATURACIÓN DETECTADO: Ciclo", punto_saturacion, "\n")
  cat("    A partir de este ciclo, la TLB dejó de registrar nuevos aciertos.\n\n")
  
  # Análisis de la fase de saturación
  fase_saturacion <- datos[punto_saturacion: nrow(datos), ]
  
  if (nrow(fase_saturacion) > 2) {
    cat(">>> ANÁLISIS DE CORRELACIÓN (Fase de Saturación)\n\n")
    
    correlacion <- cor(fase_saturacion$Procesos_Activos, fase_saturacion$TLB_Misses)
    cat("Correlación (Pearson) entre Procesos y Misses:", round(correlacion, 4), "\n")
    cat("INTERPRETACIÓN: ")
    
    if (abs(correlacion) > 0.7) {
      cat("Existe una relación", ifelse(correlacion > 0, "positiva", "negativa"), "muy fuerte.\n")
    } else if (abs(correlacion) > 0.4) {
      cat("Existe una relación", ifelse(correlacion > 0, "positiva", "negativa"), "moderada.\n")
    } else {
      cat("La relación es débil.\n")
    }
    
    if (correlacion > 0.7) {
      cat("El aumento de procesos impacta directamente en los fallos de memoria.\n\n")
    }
  }
}

# Generar gráfico de saturación
nombre_grafico <- "analisis_saturacion. png"
png(nombre_grafico, width=1200, height=800, res=120)

par(mfrow=c(2,2), mar=c(4,4,3,2))

# Gráfico 1: TLB Hits acumulados
plot(datos$Ciclo, datos$TLB_Hits,
     type="l", col="darkgreen", lwd=2,
     main="TLB Hits Acumulados",
     xlab="Ciclo", ylab="Hits Totales",
     panel.first=grid())

if (! is.na(punto_saturacion)) {
  abline(v=punto_saturacion, col="red", lty=2, lwd=2)
  text(punto_saturacion, max(datos$TLB_Hits)/2, 
       paste("Saturación\nCiclo", punto_saturacion), 
       pos=4, col="red", cex=0.9)
}

# Gráfico 2: TLB Misses
plot(datos$Ciclo, datos$TLB_Misses,
     type="l", col="darkred", lwd=2,
     main="TLB Misses Acumulados",
     xlab="Ciclo", ylab="Misses Totales",
     panel.first=grid())

if (!is.na(punto_saturacion)) {
  abline(v=punto_saturacion, col="red", lty=2, lwd=2)
}

# Gráfico 3: Hit Rate
datos$Hit_Rate <- datos$TLB_Hits / (datos$TLB_Hits + datos$TLB_Misses) * 100
plot(datos$Ciclo, datos$Hit_Rate,
     type="l", col="darkblue", lwd=2,
     main="Hit Rate a lo Largo del Tiempo",
     xlab="Ciclo", ylab="Hit Rate (%)",
     ylim=c(0, 100),
     panel.first=grid())

if (!is.na(punto_saturacion)) {
  abline(v=punto_saturacion, col="red", lty=2, lwd=2)
}
abline(h=mean(datos$Hit_Rate, na.rm=TRUE), col="orange", lty=2, lwd=2)

# Gráfico 4: Procesos vs Marcos
plot(datos$Ciclo, datos$Procesos_Activos,
     type="l", col="purple", lwd=2,
     main="Procesos Activos y Memoria Utilizada",
     xlab="Ciclo", ylab="Cantidad",
     ylim=c(0, max(c(datos$Procesos_Activos, datos$Marcos_Ocupados))),
     panel.first=grid())
lines(datos$Ciclo, datos$Marcos_Ocupados, col="brown", lwd=2)

if (!is.na(punto_saturacion)) {
  abline(v=punto_saturacion, col="red", lty=2, lwd=2)
}

legend("topleft", 
       legend=c("Procesos", "Marcos", "Punto Saturación"), 
       col=c("purple", "brown", "red"), 
       lty=c(1,1,2), lwd=2, cex=0.8)

dev.off()

cat("\n✅ Gráfico guardado en:", nombre_grafico, "\n")