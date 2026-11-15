package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.modelo.datos.ComparadorPorTiempo;
import TpoEscapeCorporativo.modelo.datos.Puntaje;
import TpoEscapeCorporativo.servicios.GestorArchivos;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla que presenta la tabla de clasificación (Top 10) con los
 * mejores puntajes y tiempos registrados de los empleados.
 */
public class PanelRanking extends JPanel {

    private final PanelPrincipal coordinadorNavegacion;
    private final GestorArchivos servicioPersistencia;

    private JLabel etiquetaTituloPrincipal;
    private JTable tablaVisualRanking;
    private DefaultTableModel modeloDatosTabla;
    private JScrollPane panelDesplazamientoTabla;
    private JButton botonVolverMenu;
    private JButton botonOrdenarPuntos;
    private JButton botonOrdenarTiempo;
    private boolean ordenarPorPuntos = true;

    /**
     * Construye el panel del Ranking Top 10 y configura su tabla y filtros de ordenamiento.
     *
     * @param coordinadorNavegacion Contenedor principal para regresar al menú de inicio.
     */
    public PanelRanking(PanelPrincipal coordinadorNavegacion) {
        this.coordinadorNavegacion = coordinadorNavegacion;
        this.servicioPersistencia = new GestorArchivos();

        configurarPropiedadesPanel();
        construirComponentesVisuales();
        configurarDisposicionYEventos();
    }

    private void configurarPropiedadesPanel() {
        this.setBackground(new Color(25, 25, 25));
        this.setLayout(null);
    }

    private void construirComponentesVisuales() {
        etiquetaTituloPrincipal = new JLabel("TOP 10 - EMPLEADOS DESTACADOS", SwingConstants.CENTER);
        etiquetaTituloPrincipal.setFont(GestorRecursos.getFuenteArcade(16f));
        etiquetaTituloPrincipal.setForeground(new Color(255, 215, 0));
        add(etiquetaTituloPrincipal);

        botonOrdenarPuntos = new JButton("MAYOR PUNTAJE");
        botonOrdenarPuntos.setFont(GestorRecursos.getFuenteArcade(8f));
        botonOrdenarPuntos.setBackground(new Color(255, 215, 0));
        botonOrdenarPuntos.setForeground(Color.BLACK);
        botonOrdenarPuntos.setFocusable(false);
        botonOrdenarPuntos.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        botonOrdenarPuntos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(botonOrdenarPuntos);

        botonOrdenarTiempo = new JButton("MENOR TIEMPO");
        botonOrdenarTiempo.setFont(GestorRecursos.getFuenteArcade(8f));
        botonOrdenarTiempo.setBackground(new Color(40, 40, 40));
        botonOrdenarTiempo.setForeground(Color.LIGHT_GRAY);
        botonOrdenarTiempo.setFocusable(false);
        botonOrdenarTiempo.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
        botonOrdenarTiempo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(botonOrdenarTiempo);

        String[] nombresColumnas = {"#", "EMPLEADO", "PUNTOS", "TIEMPO", "FECHA"};
        modeloDatosTabla = new DefaultTableModel(nombresColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVisualRanking = new JTable(modeloDatosTabla);
        configurarEstilosTabla();

        panelDesplazamientoTabla = new JScrollPane(tablaVisualRanking);
        panelDesplazamientoTabla.getViewport().setBackground(new Color(30, 30, 30));
        panelDesplazamientoTabla.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        this.add(panelDesplazamientoTabla);

        botonVolverMenu = new JButton("VOLVER AL MENU");
        botonVolverMenu.setFont(GestorRecursos.getFuenteArcade(11f));
        botonVolverMenu.setBackground(new Color(180, 40, 40));
        botonVolverMenu.setForeground(Color.WHITE);
        botonVolverMenu.setFocusable(false);
        botonVolverMenu.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        botonVolverMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.add(botonVolverMenu);
    }

    private void configurarEstilosTabla() {
        tablaVisualRanking.setFont(new Font("Monospaced", Font.BOLD, 12));
        tablaVisualRanking.setBackground(new Color(30, 30, 30));
        tablaVisualRanking.setForeground(Color.WHITE);
        tablaVisualRanking.setRowHeight(24);
        tablaVisualRanking.setGridColor(new Color(50, 50, 50));
        tablaVisualRanking.setSelectionBackground(new Color(70, 70, 20));
        tablaVisualRanking.setSelectionForeground(Color.YELLOW);

        tablaVisualRanking.getTableHeader().setFont(GestorRecursos.getFuenteArcade(9f));
        tablaVisualRanking.getTableHeader().setBackground(new Color(15, 15, 15));
        tablaVisualRanking.getTableHeader().setForeground(new Color(0, 255, 200));
        tablaVisualRanking.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderizadorCentrado = new DefaultTableCellRenderer();
        renderizadorCentrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int indiceColumna = 0; indiceColumna < tablaVisualRanking.getColumnCount(); indiceColumna++) {
            tablaVisualRanking.getColumnModel().getColumn(indiceColumna).setCellRenderer(renderizadorCentrado);
        }

        tablaVisualRanking.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaVisualRanking.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaVisualRanking.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaVisualRanking.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaVisualRanking.getColumnModel().getColumn(4).setPreferredWidth(135);
    }

    private void configurarDisposicionYEventos() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int anchoVentana = getWidth();
                int altoVentana = getHeight();

                int anchoTitulo = (int) (anchoVentana * 0.85);
                int xTitulo = (anchoVentana - anchoTitulo) / 2;
                int yTitulo = (int) (altoVentana * 0.05);
                etiquetaTituloPrincipal.setBounds(xTitulo, yTitulo, anchoTitulo, 30);

                int anchoBotonFiltro = 160;
                int altoBotonFiltro = 24;
                int yFiltros = (int) (altoVentana * 0.12);
                botonOrdenarPuntos.setBounds(anchoVentana / 2 - anchoBotonFiltro - 10, yFiltros, anchoBotonFiltro, altoBotonFiltro);
                botonOrdenarTiempo.setBounds(anchoVentana / 2 + 10, yFiltros, anchoBotonFiltro, altoBotonFiltro);

                int anchoTabla = (int) (anchoVentana * 0.88);
                int xTabla = (anchoVentana - anchoTabla) / 2;
                int yTabla = (int) (altoVentana * 0.18);
                int altoTabla = (int) (altoVentana * 0.63);
                panelDesplazamientoTabla.setBounds(xTabla, yTabla, anchoTabla, altoTabla);

                int anchoBoton = (int) (anchoVentana * 0.40);
                int xBoton = (anchoVentana - anchoBoton) / 2;
                int yBoton = (int) (altoVentana * 0.85);
                botonVolverMenu.setBounds(xBoton, yBoton, anchoBoton, 40);
            }
        });

        botonVolverMenu.addActionListener(eventoClick -> coordinadorNavegacion.mostrarPanel("INICIO"));

        botonOrdenarPuntos.addActionListener(e -> {
            if (!ordenarPorPuntos) {
                ordenarPorPuntos = true;
                actualizarEstiloBotonesFiltro();
                cargarYRefrescarDatos();
            }
        });

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
     * Visibilidad de paquete (default): invocada por {@link PanelPrincipal} al navegar al ranking.
     */
    void cargarYRefrescarDatos() {
        modeloDatosTabla.setRowCount(0);

        List<Puntaje> listaMejoresPuntajes = servicioPersistencia.cargarMejoresPuntajes();
        if (listaMejoresPuntajes.isEmpty()) {
            modeloDatosTabla.addRow(new Object[]{"-", "SIN REGISTROS AUN", "0 pts", "00:00", "--/--/----"});
            return;
        }

        if (this.ordenarPorPuntos) {
            Collections.sort(listaMejoresPuntajes);
        } else {
            Collections.sort(listaMejoresPuntajes, new ComparadorPorTiempo());
        }

        int posicionEscalafon = 1;
        for (Puntaje puntajeActual : listaMejoresPuntajes) {
            Object[] datosFila = new Object[]{
                    "#" + posicionEscalafon,
                    puntajeActual.getNombreJugador(),
                    puntajeActual.getPuntos() + " pts",
                    puntajeActual.getTiempoFormateado(),
                    puntajeActual.getFechaFormateada()
            };

            modeloDatosTabla.addRow(datosFila);
            posicionEscalafon++;
        }
    }
}
