package TpoEscapeCorporativo.servicios;

import TpoEscapeCorporativo.excepciones.NivelNoEncontradoException;
import TpoEscapeCorporativo.modelo.datos.Puntaje;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio encargado de la persistencia de datos del juego.
 * Gestiona el almacenamiento de puntajes mediante serialización
 * y la carga de mapas/niveles desde archivos de texto plano.
 */
public class GestorArchivos {

    private static final String RUTA_RANKING_DEFAULT = "data/ranking.dat";
    private static final String CARPETA_NIVELES_DEFAULT = "assets/mapas/";
    private static final int MAX_TOP_PUNTAJES = 10;

    private final String rutaRanking;
    private final String carpetaNiveles;

    /**
     * Constructor sobrecargado con rutas por defecto.
     */
    public GestorArchivos() {
        this(RUTA_RANKING_DEFAULT, CARPETA_NIVELES_DEFAULT);
    }

    /**
     * Constructor principal que permite inyectar las rutas de acceso a archivos.
     *
     * @param rutaRanking    Ruta del archivo binario de mejores puntajes.
     * @param carpetaNiveles Directorio base donde se alojan los archivos de nivel.
     */
    public GestorArchivos(String rutaRanking, String carpetaNiveles) {
        this.rutaRanking = rutaRanking;
        this.carpetaNiveles = (carpetaNiveles.endsWith(File.separator) || carpetaNiveles.endsWith("/"))
                ? carpetaNiveles
                : carpetaNiveles + File.separator;
    }

    /**
     * Carga la lista de mejores puntajes serializados en disco.
     * Si el archivo no existe o se encuentra vacío (primera ejecución), retorna
     * una lista vacía de forma silenciosa para no interrumpir el flujo.
     *
     * @return Lista de puntajes ordenados de mayor a menor.
     */
    @SuppressWarnings("unchecked")
    public List<Puntaje> cargarMejoresPuntajes() {
        File archivo = new File(this.rutaRanking);

        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object leido = ois.readObject();
            if (leido instanceof List<?>) {
                List<Puntaje> listaPuntajes = (List<Puntaje>) leido;
                Collections.sort(listaPuntajes);
                return listaPuntajes;
            }
        } catch (FileNotFoundException e) {
            // Archivo inexistente en primera ejecución
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            // Archivo corrupto o versión incompatible: devolvemos lista vacía para no colapsar la app
            System.err.println("Advertencia: No se pudo leer el archivo de ranking: " + e.getMessage());
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    /**
     * Agrega nuevo puntaje, ordena el ranking, lo trunca al TOP 10 y lo serializa en disco.
     *
     * @param nuevoPuntaje Objeto Puntaje a registrar.
     * @throws IOException Si ocurre un fallo crítico de escritura en el sistema de archivos.
     */
    public void guardarPuntaje(Puntaje nuevoPuntaje) throws IOException {
        if (nuevoPuntaje == null) {
            throw new IllegalArgumentException("El puntaje a guardar no puede ser nulo");
        }

        List<Puntaje> puntajes = cargarMejoresPuntajes();
        puntajes.add(nuevoPuntaje);

        // Ordenamiento natural (implementado en Puntaje via Comparable)
        Collections.sort(puntajes);

        // Conservar únicamente los mejores N registros
        if (puntajes.size() > MAX_TOP_PUNTAJES) {
            puntajes = new ArrayList<>(puntajes.subList(0, MAX_TOP_PUNTAJES));
        }

        // Asegurar la existencia del directorio contenedor
        File archivo = new File(this.rutaRanking);
        File directorioPadre = archivo.getParentFile();
        if (directorioPadre != null && !directorioPadre.exists()) {
            directorioPadre.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(puntajes);
        }
    }

    /**
     * Carga la matriz de diseño de un nivel desde un archivo .txt plano.
     *
     * @param numeroNivel Identificador numérico del nivel (ej. 1 -> nivel_1.txt).
     * @return Matriz de caracteres (char[][]) que representa la cuadrícula del nivel.
     * @throws NivelNoEncontradoException Si el archivo no existe o no se puede leer.
     */
    public char[][] cargarMapaNivel(int numeroNivel) throws NivelNoEncontradoException {
        String nombreArchivo = this.carpetaNiveles + "nivel_" + numeroNivel + ".txt";
        File archivo = new File(nombreArchivo);

        if (!archivo.exists() || !archivo.isFile()) {
            throw new NivelNoEncontradoException(
                    "No se encontró el archivo de diseño para el nivel " + numeroNivel + " en: " + nombreArchivo
            );
        }

        List<String> filas;
        try {
            filas = leerFilasNivel(archivo);
        } catch (IOException e) {
            throw new NivelNoEncontradoException(
                    "Error al leer el archivo del nivel " + numeroNivel + ": " + nombreArchivo,
                    e
            );
        }

        if (filas.isEmpty()) {
            throw new NivelNoEncontradoException("El archivo del nivel " + numeroNivel + " está vacío.");
        }

        // Convertir la lista de cadenas en una matriz de caracteres char[][]
        int totalFilas = filas.size();
        int totalColumnas = 0;
        for (String fila : filas) {
            totalColumnas = Math.max(totalColumnas, fila.length());
        }

        char[][] mapa = new char[totalFilas][totalColumnas];

        for (int i = 0; i < totalFilas; i++) {
            String filaActual = filas.get(i);
            for (int j = 0; j < totalColumnas; j++) {
                // Rellenar con espacio si alguna fila fuese más corta para evitar IndexOutOfBounds
                mapa[i][j] = (j < filaActual.length()) ? filaActual.charAt(j) : ' ';
            }
        }

        return mapa;
    }

    /**
     * Lee todas las filas válidas de un archivo de nivel en UTF-8.
     *
     * @param archivo Archivo de texto del nivel.
     * @return Lista de filas leídas.
     * @throws IOException Si ocurre un error durante la lectura de I/O.
     */
    private List<String> leerFilasNivel(File archivo) throws IOException {
        List<String> filas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    filas.add(linea);
                }
            }
        }

        return filas;
    }

    public String getRutaRanking() {
        return rutaRanking;
    }

    public String getRutaRankingDefault() {
        return rutaRanking;
    }

    public String getCarpetaNiveles() {
        return carpetaNiveles;
    }
}
