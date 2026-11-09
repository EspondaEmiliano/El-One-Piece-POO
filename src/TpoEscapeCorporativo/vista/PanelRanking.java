package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.modelo.datos.Puntaje;
import TpoEscapeCorporativo.servicios.GestorArchivos;
import TpoEscapeCorporativo.modelo.datos.ComparadorPorTiempo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla que presenta la tabla de clasificación (Top 10) con los
 * mejores puntajes y tiempos registrados de los empleados
 */
public class PanelRanking extends JPanel {

    private final PanelPrincipal coordinadorNavegacion;
    private final GestorArchivos servicioPersistencia;

    // Componentes visuales
    private JLabel etiquetaTituloPrincipal;
    private JTable tablaVisualRanking;
    private DefaultTableModel modeloDatosTabla;
    private JScrollPane panelDesplazamientoTabla;
    private JButton botonVolverMenu;
    private JButton botonOrdenarPuntos;
    private JButton botonOrdenarTiempo;
    private boolean ordenarPorPuntos = true; // Por defecto orden natural (Puntos)


    public PanelRanking(PanelPrincipal coordinadorNavegacion) {
        this.coordinadorNavegacion = coordinadorNavegacion;
        this.servicioPersistencia = new GestorArchivos();

        configurarPropiedadesPanel();
        construirComponentesVisuales();
        configurarDisposicionYEventos();
    }

    /**
     * Configura el fondo y el gestor de diseño del panel.
     */
    private void configurarPropiedadesPanel() {
        this.setBackground(new Color(25,25,25));
        this.setLayout(null);
    }

    /**
     * Inicializa los componentes de la interfaz de usuario con estilo arcade.
     */
    private void construirComponentesVisuales() {
        // Título superior
        etiquetaTituloPrincipal = new JLabel("TOP 10 - EMPLEADOS DESTACADOS",SwingConstants.CENTER);
        etiquetaTituloPrincipal.setFont(GestorRecursos.getFuenteArcade(16f));
        etiquetaTituloPrincipal.setForeground(new Color(255,215,0));
        add(etiquetaTituloPrincipal);

        // Botón filtró: Mayor Puntaje (Comparable)
        botonOrdenarPuntos = new JButton("MAYOR PUNTAJE");
        botonOrdenarPuntos.setFont(GestorRecursos.getFuenteArcade(8f));
        botonOrdenarPuntos.setBackground(new Color(255, 215, 0));
        botonOrdenarPuntos.setForeground(Color.BLACK);
        botonOrdenarPuntos.setFocusable(false);
        botonOrdenarPuntos.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        botonOrdenarPuntos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(botonOrdenarPuntos);

        // Botón filtró: Menor Tiempo / Speedrun (Comparator)
        botonOrdenarTiempo = new JButton("MENOR TIEMPO");
        botonOrdenarTiempo.setFont(GestorRecursos.getFuenteArcade(8f));
        botonOrdenarTiempo.setBackground(new Color(40, 40, 40));
        botonOrdenarTiempo.setForeground(Color.LIGHT_GRAY);
        botonOrdenarTiempo.setFocusable(false);
        botonOrdenarTiempo.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
        botonOrdenarTiempo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(botonOrdenarTiempo);

        // Modelo de datos de la tabla (no editable por el usuario)
        String [] nombresColumnas = {"#", "EMPLEADO", "PUNTOS", "TIEMPO", "FECHA"};
        modeloDatosTabla = new DefaultTableModel(nombresColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tabla visual y estilización retro
        tablaVisualRanking = new JTable(modeloDatosTabla);
        configurarEstilosTabla();

        panelDesplazamientoTabla = new JScrollPane(tablaVisualRanking);
        panelDesplazamientoTabla.getViewport().setBackground(new Color(30,30,30));
        panelDesplazamientoTabla.setBorder(BorderFactory.createLineBorder(new Color(60,60,60), 1));
        this.add(panelDesplazamientoTabla);

        // Botón inferior para volver al menú
        botonVolverMenu = new JButton("VOLVER AL MENU");
        botonVolverMenu.setFont(GestorRecursos.getFuenteArcade(11f));
        botonVolverMenu.setBackground(new Color(180, 40, 40));
        botonVolverMenu.setForeground(Color.white);
        botonVolverMenu.setFocusable(false);
        botonVolverMenu.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botonVolverMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.add(botonVolverMenu);

    }

    /**
     * Aplica los colores oscuros, fuentes y alineación centrada a las celdas de la tabla.
     */
    private void configurarEstilosTabla() {
        tablaVisualRanking.setFont(new Font("Monospaced", Font.BOLD, 12));
        tablaVisualRanking.setBackground(new Color(30,30,30));
        tablaVisualRanking.setForeground(Color.WHITE);
        tablaVisualRanking.setRowHeight(24);
        tablaVisualRanking.setGridColor(new Color(50, 50, 50));
        tablaVisualRanking.setSelectionBackground(new Color(70, 70, 20));
        tablaVisualRanking.setSelectionForeground(Color.YELLOW);

        // Encabezado de la tabla
        tablaVisualRanking.getTableHeader().setFont(GestorRecursos.getFuenteArcade(9f));
        tablaVisualRanking.getTableHeader().setBackground(new Color(15, 15, 15));
        tablaVisualRanking.getTableHeader().setForeground(new Color(0, 255, 200));
        tablaVisualRanking.getTableHeader().setReorderingAllowed(false);

        // Renderizador para centrar el contenido de las columnas
        DefaultTableCellRenderer renderizadorCentrado = new DefaultTableCellRenderer();
        renderizadorCentrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int indiceColumna = 0; indiceColumna < tablaVisualRanking.getColumnCount(); indiceColumna++) {
            tablaVisualRanking.getColumnModel().getColumn(indiceColumna).setCellRenderer(renderizadorCentrado);
        }

        // Anchos proporcionales de columnas
        tablaVisualRanking.getColumnModel().getColumn(0).setPreferredWidth(35);  // #
        tablaVisualRanking.getColumnModel().getColumn(1).setPreferredWidth(160); // Empleado
        tablaVisualRanking.getColumnModel().getColumn(2).setPreferredWidth(90);  // Puntos
        tablaVisualRanking.getColumnModel().getColumn(3).setPreferredWidth(80);  // Tiempo
        tablaVisualRanking.getColumnModel().getColumn(4).setPreferredWidth(135); // Fecha
    }

    /**
     * Define las dimensiones proporcionales de los componentes y los listeners de acción.
     */
    private void configurarDisposicionYEventos() {
        // Hacemos que el panel escuche cuando cambia de tamaño
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int anchoVentana = getWidth();
                int altoVentana = getHeight();

                // 1. Título superior: centrado y ancho proporcional (85% del ancho de la ventana)
                int anchoTitulo = (int) (anchoVentana * 0.85);
                int xTitulo = (anchoVentana - anchoTitulo) / 2;
                int yTitulo = (int) (altoVentana * 0.05); // 5% desde arriba
                etiquetaTituloPrincipal.setBounds(xTitulo, yTitulo, anchoTitulo, 30);

                // Posicionamiento de botones de ordenamiento entre el título y la tabla
                int anchoBotonFiltro = 160;
                int altoBotonFiltro = 24;
                int yFiltros = (int) (altoVentana * 0.12);
                botonOrdenarPuntos.setBounds(anchoVentana / 2 - anchoBotonFiltro - 10, yFiltros, anchoBotonFiltro, altoBotonFiltro);
                botonOrdenarTiempo.setBounds(anchoVentana / 2 + 10, yFiltros, anchoBotonFiltro, altoBotonFiltro);


                // 2. Tabla / JScrollPane: ocupa el centro con márgenes proporcionales
                int anchoTabla = (int) (anchoVentana * 0.88);
                int xTabla = (anchoVentana - anchoTabla) / 2;
                int yTabla = (int) (altoVentana * 0.18); // Debajo del título
                int altoTabla = (int) (altoVentana * 0.63); // Ocupa el 65% del alto
                panelDesplazamientoTabla.setBounds(xTabla, yTabla, anchoTabla, altoTabla);

                // 3. Botón inferior: centrado horizontalmente cerca del pie de página
                int anchoBoton = (int) (anchoVentana * 0.40); // 40% del ancho de la ventana
                int xBoton = (anchoVentana - anchoBoton) / 2;
                int yBoton = (int) (altoVentana * 0.85); // 85% hacia abajo
                int altoBoton = 40;
                botonVolverMenu.setBounds(xBoton, yBoton, anchoBoton, altoBoton);
            }
        });

        // Evento de clic del botón
        botonVolverMenu.addActionListener(eventoClick -> {
            coordinadorNavegacion.mostrarPanel("INICIO");
        });

        // Evento: ordenar por puntos (Comparable)
        botonOrdenarPuntos.addActionListener(e -> {
            if (!ordenarPorPuntos) {
                ordenarPorPuntos = true;
                actualizarEstiloBotonesFiltro();
                cargarYRefrescarDatos();
            }
        });

        // Evento: ordenar por tiempo (Comparator)
        botonOrdenarTiempo.addActionListener(e -> {
            if (ordenarPorPuntos) {
                ordenarPorPuntos = false;
                actualizarEstiloBotonesFiltro();
                cargarYRefrescarDatos();
            }
        });
    }

    private void actualizarEstiloBotonesFiltro() {
        if (ordenarPorPuntos) {
            botonOrdenarPuntos.setBackground(new Color(255, 215, 0));
            botonOrdenarPuntos.setForeground(Color.BLACK);
            botonOrdenarTiempo.setBackground(new Color(40, 40, 40));
            botonOrdenarTiempo.setForeground(Color.LIGHT_GRAY);
        } else {
            botonOrdenarTiempo.setBackground(new Color(0, 200, 255));
            botonOrdenarTiempo.setForeground(Color.BLACK);
            botonOrdenarPuntos.setBackground(new Color(40, 40, 40));
            botonOrdenarPuntos.setForeground(Color.LIGHT_GRAY);
        }
    }

    /**
     * Lee la lista de mejores puntajes desde el archivo binario y puebla la tabla.
     * Debe invocarse cada vez que se navega hacia este panel.
     */
    public void cargarYRefrescarDatos() {
        modeloDatosTabla.setRowCount(0); // Limpia filas anteriores

        List<Puntaje> listaMejoresPuntajes = servicioPersistencia.cargarMejoresPuntajes();
        DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (listaMejoresPuntajes.isEmpty()) {
            modeloDatosTabla.addRow(new Object[]{"-", "SIN REGISTROS AUN", "0 pts", "00:00", "--/--/----"});
            return;
        }

        // Aplicación polimórfica de ordenamiento según el criterio seleccionado:
        if (this.ordenarPorPuntos) {
            // Orden natural: utiliza Comparable<Puntaje> definido en Puntaje.compareTo()
            Collections.sort(listaMejoresPuntajes);
        } else {
            // Orden personalizado: utiliza Comparator<Puntaje> vía ComparadorPorTiempo
            Collections.sort(listaMejoresPuntajes, new ComparadorPorTiempo());
        }

        int posicionEscalafon = 1;
        for (Puntaje puntajeActual : listaMejoresPuntajes) {
            String fechaFormateada = (puntajeActual.getFechaRegistro() != null)
                    ? puntajeActual.getFechaRegistro().format(formateadorFecha)
                    : "Fecha desc.";

            Object[] datosFila = new Object[]{
                    "#" + posicionEscalafon,
                    puntajeActual.getNombreJugador(),
                    puntajeActual.getPuntos() + " pts",
                    puntajeActual.getTiempoFormateado(),
                    fechaFormateada
            };

            modeloDatosTabla.addRow(datosFila);
            posicionEscalafon++;
        }
    }
}
