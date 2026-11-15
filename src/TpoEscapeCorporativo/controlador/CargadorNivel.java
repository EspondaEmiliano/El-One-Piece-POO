package TpoEscapeCorporativo.controlador;

import TpoEscapeCorporativo.excepciones.NivelNoEncontradoException;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.entidades.Empleado;
import TpoEscapeCorporativo.modelo.entidades.Jefe;
import TpoEscapeCorporativo.modelo.entidades.Muro;
import TpoEscapeCorporativo.modelo.entidades.Salida;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.Corazon;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;
import TpoEscapeCorporativo.servicios.GestorArchivos;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsable de construir las entidades jugables de un nivel a partir de la matriz
 * de caracteres leída desde disco y de parametrizar la dificultad del Jefe según el piso.
 * Separa la lógica de parseo de mapas del bucle de simulación en {@link JuegoEngine}.
 */
public class CargadorNivel {

    /** Tamaño estándar de cada celda del tablero en píxeles. */
    public static final int TAMANIO_CELDA = 32;
    /** Desplazamiento horizontal del tablero dentro del área visible de 600 px. */
    public static final int MARGEN_HORIZONTAL_PX = 12;
    /** Desplazamiento vertical del tablero dentro del área de juego de 400 px. */
    public static final int MARGEN_VERTICAL_PX = 8;
    /** Límite vertical inferior a partir del cual un proyectil abandona el área jugable. */
    public static final int LIMITE_INFERIOR_ARENA_Y = 400;

    private static final int VELOCIDAD_EMPLEADO_DEFAULT = 2;
    private static final int VELOCIDAD_JEFE_DEFAULT = 1;
    private static final int MARGEN_HITBOX_EMPLEADO = 4;

    private final GestorArchivos gestorArchivos;

    /**
     * Construye el cargador asociándolo al servicio de acceso a archivos.
     *
     * @param gestorArchivos Servicio que provee la matriz del mapa desde disco.
     */
    public CargadorNivel(GestorArchivos gestorArchivos) {
        this.gestorArchivos = gestorArchivos;
    }

    /**
     * Carga un nivel desde disco, instancia todas sus entidades en sus coordenadas
     * calibradas y retorna el contenedor con el estado inicial del mapa.
     *
     * @param numeroNivel   Índice numérico del nivel a construir.
     * @param totalNiveles  Cantidad total de niveles detectados en el juego.
     * @return Instancia de {@link DatosNivel} con las entidades creadas.
     * @throws NivelNoEncontradoException Si el archivo del mapa no existe o es ilegible.
     */
    public DatosNivel cargar(int numeroNivel, int totalNiveles) throws NivelNoEncontradoException {
        char[][] mapa = this.gestorArchivos.cargarMapaNivel(numeroNivel);

        List<Muro> muros = new ArrayList<>();
        List<ZonaBuffo> zonasBuffo = new ArrayList<>();
        Empleado jugador = null;
        Jefe enemigo = null;
        Salida salida = null;

        for (int fila = 0; fila < mapa.length; fila++) {
            for (int columna = 0; columna < mapa[fila].length; columna++) {
                int x1 = columna * TAMANIO_CELDA + MARGEN_HORIZONTAL_PX;
                int y1 = fila * TAMANIO_CELDA + MARGEN_VERTICAL_PX;
                Posicion posicionCelda = new Posicion(x1, y1, x1 + TAMANIO_CELDA, y1 + TAMANIO_CELDA);

                switch (mapa[fila][columna]) {
                    case '#':
                        muros.add(new Muro(posicionCelda));
                        break;
                    case 'E':
                        Posicion posicionEmpleado = new Posicion(
                                x1 + MARGEN_HITBOX_EMPLEADO,
                                y1 + MARGEN_HITBOX_EMPLEADO,
                                x1 + TAMANIO_CELDA - MARGEN_HITBOX_EMPLEADO,
                                y1 + TAMANIO_CELDA - MARGEN_HITBOX_EMPLEADO
                        );
                        jugador = new Empleado(posicionEmpleado, VELOCIDAD_EMPLEADO_DEFAULT);
                        break;
                    case 'J':
                        enemigo = crearJefeParaNivel(posicionCelda, numeroNivel, totalNiveles);
                        break;
                    case 'S':
                        salida = new Salida(posicionCelda);
                        break;
                    case 'C':
                        zonasBuffo.add(new ZonaBuffo(posicionCelda, new Cafe()));
                        break;
                    case 'A':
                        zonasBuffo.add(new ZonaBuffo(posicionCelda, new Aguinaldo()));
                        break;
                    case 'V':
                    case 'H':
                        zonasBuffo.add(new ZonaBuffo(posicionCelda, new Corazon()));
                        break;
                    default:
                        break;
                }
            }
        }

        return new DatosNivel(muros, zonasBuffo, jugador, enemigo, salida);
    }

    /**
     * Instancia al Jefe configurando su cadencia y modalidad de disparo según la dificultad del nivel.
     */
    private Jefe crearJefeParaNivel(Posicion posicion, int numeroNivel, int totalNiveles) {
        int cadenciaJefe;
        boolean ataqueTriple = false;

        if (numeroNivel == 5 || numeroNivel >= totalNiveles) {
            cadenciaJefe = 2000;
            ataqueTriple = true;
        } else {
            switch (numeroNivel) {
                case 1:
                    cadenciaJefe = 2000;
                    break;
                case 2:
                    cadenciaJefe = 1700;
                    break;
                case 3:
                    cadenciaJefe = 1550;
                    break;
                default:
                    cadenciaJefe = 1250;
                    break;
            }
        }

        return new Jefe(posicion, VELOCIDAD_JEFE_DEFAULT, cadenciaJefe, ataqueTriple);
    }

    /**
     * Retorna una etiqueta descriptiva de dificultad asociada a cada nivel.
     *
     * @param numeroNivel Identificador numérico del nivel.
     * @return Etiqueta legible de dificultad para mostrar en la interfaz.
     */
    public String getEtiquetaDificultad(int numeroNivel) {
        switch (numeroNivel) {
            case 1:
                return "DIF: BAJA";
            case 2:
                return "DIF: MEDIA";
            case 3:
                return "DIF: ALTA";
            case 4:
                return "DIF: EXTREMA";
            case 5:
                return "DIF: JEFE FINAL";
            default:
                return "DIF: DESAFIO";
        }
    }

    /**
     * Contenedor inmutable (clase interna estática) con las entidades generadas al cargar un nivel.
     */
    public static class DatosNivel {
        private final List<Muro> muros;
        private final List<ZonaBuffo> zonasBuffo;
        private final Empleado jugador;
        private final Jefe enemigo;
        private final Salida salida;

        /**
         * Inicializa el paquete de entidades construidas para un nivel.
         *
         * @param muros      Lista de muros del laberinto.
         * @param zonasBuffo Lista de ítems/buffos ubicados en el mapa.
         * @param jugador    Instancia inicial del empleado.
         * @param enemigo    Instancia inicial del jefe.
         * @param salida     Puerta de escape del nivel.
         */
        public DatosNivel(List<Muro> muros, List<ZonaBuffo> zonasBuffo, Empleado jugador, Jefe enemigo, Salida salida) {
            this.muros = muros;
            this.zonasBuffo = zonasBuffo;
            this.jugador = jugador;
            this.enemigo = enemigo;
            this.salida = salida;
        }

        /** @return Lista de muros del nivel cargado. */
        public List<Muro> getMuros() { return muros; }

        /** @return Lista de zonas de buffo del nivel cargado. */
        public List<ZonaBuffo> getZonasBuffo() { return zonasBuffo; }

        /** @return Empleado posicionado en su punto de partida. */
        public Empleado getJugador() { return jugador; }

        /** @return Jefe configurado para el nivel. */
        public Jefe getEnemigo() { return enemigo; }

        /** @return Puerta de salida del nivel. */
        public Salida getSalida() { return salida; }
    }
}
