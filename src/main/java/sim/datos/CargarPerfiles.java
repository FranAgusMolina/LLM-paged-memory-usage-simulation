package sim.datos;

import sim.modelo.Perfil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase encargada de cargar perfiles de configuración desde un archivo de texto.
 * Lee el archivo perfiles.txt y devuelve un Map ordenado donde el primer perfil leído es el primero en salir.
 */
public class CargarPerfiles {

    private static final String ARCHIVO_PERFILES = "perfiles.txt";

    /**
     * Carga los perfiles desde el archivo perfiles.txt ubicado en resources.
     * Devuelve un LinkedHashMap que mantiene el orden de inserción (el primero leído es el primero al iterar).
     *
     * Formato esperado del archivo:
     * [NombrePerfil]
     * columnas=46
     * filas=18
     * tamPagina=64
     * tamTlb=32
     * velocidad=100
     *
     * [OtroPerfil]
     * ...
     *
     * @return Map con clave=nombre del perfil y valor=objeto Perfil
     */
    public static LinkedHashMap<String, Perfil> cargar() {
        LinkedHashMap<String, Perfil> perfiles = new LinkedHashMap<>();

        try (InputStream inputStream = CargarPerfiles.class.getClassLoader().getResourceAsStream(ARCHIVO_PERFILES)) {
            if (inputStream == null) {
                System.err.println("❌ No se encontró el archivo: " + ARCHIVO_PERFILES);
                return perfiles;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String linea;
                String nombrePerfil = null;
                Map<String, String> propiedades = new LinkedHashMap<>();

                while ((linea = reader.readLine()) != null) {
                    linea = linea.trim();

                    // Ignorar líneas vacías y comentarios
                    if (linea.isEmpty() || linea.startsWith("#")) {
                        continue;
                    }

                    // Detectar inicio de nuevo perfil [NombrePerfil]
                    if (linea.startsWith("[") && linea.endsWith("]")) {
                        // Guardar el perfil anterior si existe
                        if (nombrePerfil != null && !propiedades.isEmpty()) {
                            Perfil perfil = crearPerfil(nombrePerfil, propiedades);
                            if (perfil != null) {
                                perfiles.put(nombrePerfil, perfil);
                            }
                        }

                        // Iniciar nuevo perfil
                        nombrePerfil = linea.substring(1, linea.length() - 1).trim();
                        propiedades = new LinkedHashMap<>();
                    }
                    // Leer propiedad clave=valor
                    else if (linea.contains("=")) {
                        String[] partes = linea.split("=", 2);
                        if (partes.length == 2) {
                            propiedades.put(partes[0].trim(), partes[1].trim());
                        }
                    }
                }

                // Guardar el último perfil
                if (nombrePerfil != null && !propiedades.isEmpty()) {
                    Perfil perfil = crearPerfil(nombrePerfil, propiedades);
                    if (perfil != null) {
                        perfiles.put(nombrePerfil, perfil);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo de perfiles: " + e.getMessage());
            e.printStackTrace();
        }

        return perfiles;
    }

    /**
     * Crea un objeto Perfil a partir de las propiedades leídas.
     */
    private static Perfil crearPerfil(String nombre, Map<String, String> props) {
        try {
            int columnas = Integer.parseInt(props.getOrDefault("columnas", "0"));
            int filas = Integer.parseInt(props.getOrDefault("filas", "0"));
            int tamPagina = Integer.parseInt(props.getOrDefault("tamPagina", "0"));
            int tamTlb = Integer.parseInt(props.getOrDefault("tamTlb", "0"));
            int velocidad = Integer.parseInt(props.getOrDefault("velocidad", "100"));

            if (columnas <= 0 || filas <= 0 || tamPagina <= 0 || tamTlb <= 0) {
                System.err.println("⚠️ Perfil inválido: " + nombre + " (valores negativos o cero)");
                return null;
            }

            return new Perfil(nombre, columnas, filas, tamPagina, velocidad, tamTlb);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error al parsear perfil: " + nombre + " - " + e.getMessage());
            return null;
        }
    }
}
