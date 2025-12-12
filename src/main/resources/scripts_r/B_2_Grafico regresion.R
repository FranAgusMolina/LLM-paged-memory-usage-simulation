# --- 1. CONFIGURACION Y ARGUMENTOS ---
args <- commandArgs(trailingOnly = TRUE)

if (length(args) < 1) {
  stop("Error: No se proporciono la ruta del archivo CSV.")
}

archivo_csv <- args[1]
directorio_salida <- if (length(args) >= 2) args[2] else "."

if (!file.exists(archivo_csv)) {
  stop("El archivo no existe")
}

# Construir ruta de salida en el directorio temporal
nombre_imagen <- "grafico_regresion.png"
ruta_imagen <- file.path(directorio_salida, nombre_imagen)

# --- 2. CARGA Y PREPARACION ---
datos <- read.csv(archivo_csv, header = TRUE, sep = ",")

# Detectar punto de corte
diferencias_hits <- diff(datos$TLB_Hits)
indice_freno <- which(diferencias_hits == 0)[1]

# Abrimos PNG
png(filename = ruta_imagen, width = 800, height = 600)

# Graficamos los puntos base (todos)
plot(datos$Ciclo, datos$TLB_Misses,
     main = "Analisis de Regresion: Estabilidad vs. Saturacion",
     xlab = "Ciclo",
     ylab = "TLB Misses",
     pch = 20,           # Puntos solidos pequeños
     col = "gray",       # Color gris para los datos crudos
     las = 1)

if (!is.na(indice_freno)) {
  ciclo_corte <- datos$Ciclo[indice_freno]
  
  # Dividimos los datos en dos fases
  fase_estable <- subset(datos, Ciclo <= ciclo_corte)
  fase_critica <- subset(datos, Ciclo > ciclo_corte)
  
  # --- FASE 1: ESTABLE (Color Verde) ---
  if(nrow(fase_estable) > 1) {
    modelo1 <- lm(TLB_Misses ~ Ciclo, data = fase_estable)
    # Dibujamos la recta (solo en el rango de la fase 1)
    # Usamos clip() para que la linea no se pase del ciclo de corte
    clip(min(datos$Ciclo), ciclo_corte, min(datos$TLB_Misses), max(datos$TLB_Misses))
    abline(modelo1, col = "darkgreen", lwd = 3)
  }
  
  # --- FASE 2: CRITICA (Color Rojo) ---
  if(nrow(fase_critica) > 1) {
    modelo2 <- lm(TLB_Misses ~ Ciclo, data = fase_critica)
    # Restauramos el area de dibujo completa y recortamos para la fase 2
    par(usr = par("usr")) # Reset del clip
    clip(ciclo_corte, max(datos$Ciclo), min(datos$TLB_Misses), max(datos$TLB_Misses))
    abline(modelo2, col = "red", lwd = 3)
    
    # Calculamos la pendiente para mostrarla
    pendiente <- round(coef(modelo2)[2], 2)
    titulo_pendiente <- paste("Pendiente Fase Critica:", pendiente, "misses/ciclo")
    
    # Leyenda
    legend("topleft", 
           legend = c("Fase Estable (Memoria OK)", "Fase Saturada (Thrashing)", titulo_pendiente),
           col = c("darkgreen", "red", "white"), 
           lty = c(1, 1, 0), 
           lwd = c(3, 3, 0),
           bg = "white")
  }
  
} else {
  # Si no hay saturacion, una sola linea azul
  modelo <- lm(TLB_Misses ~ Ciclo, data = datos)
  abline(modelo, col = "blue", lwd = 2)
  text(x=mean(datos$Ciclo), y=mean(datos$TLB_Misses), labels="Comportamiento Lineal Constante")
}

dev.off()

cat(paste(">>> GRAFICO DE REGRESION GENERADO: ", basename(ruta_imagen), "\n"))