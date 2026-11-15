package TpoEscapeCorporativo.vista;

import TpoEscapeCorporativo.controlador.JuegoEngine;
import TpoEscapeCorporativo.interfaces.Buffo;
import TpoEscapeCorporativo.modelo.datos.EstadoJuego;
import TpoEscapeCorporativo.modelo.datos.Posicion;
import TpoEscapeCorporativo.modelo.entidades.Empleado;
import TpoEscapeCorporativo.modelo.entidades.Jefe;
import TpoEscapeCorporativo.modelo.entidades.Muro;
import TpoEscapeCorporativo.modelo.entidades.Proyectil;
import TpoEscapeCorporativo.modelo.entidades.Salida;
import TpoEscapeCorporativo.modelo.items.Aguinaldo;
import TpoEscapeCorporativo.modelo.items.Cafe;
import TpoEscapeCorporativo.modelo.items.Corazon;
import TpoEscapeCorporativo.modelo.items.ZonaBuffo;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Encargado exclusivo del renderizado gráfico 2D del tablero de juego,
 * las entidades activas, el HUD superior y los carteles superpuestos de estado.
 * Visibilidad de paquete (default) para separar el dibujado del Game Loop en {@link PanelJuego}.
 */
class RenderizadorJuego {

    private static final int ANCHO_BASE = PanelJuego.ANCHO;
    private static final int ALTO_BASE = PanelJuego.ALTO;
    private static final int ALTO_HUD = PanelJuego.ALTO_HUD;

    /**
     * Renderiza un frame completo de la partida aplicando escalado proporcional al tamaño del panel.
     *
     * @param g           Contexto gráfico provisto por Swing.
     * @param anchoActual Ancho actual del panel en pantalla.
     * @param altoActual  Alto actual del panel en pantalla.
     * @param motor       Motor con el estado actual de entidades, puntajes y reloj.
     * @param observador  Observador de imágenes para sprites animados GIF/PNG.
     */
    void renderizar(Graphics g, int anchoActual, int altoActual, JuegoEngine motor, ImageObserver observador) {
        double escalaX = (double) anchoActual / ANCHO_BASE;
        double escalaY = (double) altoActual / ALTO_BASE;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.scale(escalaX, escalaY);

        dibujarHUD(g2d, motor);

        Graphics2D gJuego = (Graphics2D) g2d.create();
        gJuego.translate(0, ALTO_HUD);

        dibujarMapa(gJuego, motor, observador);
        dibujarBuffos(gJuego, motor, observador);
        dibujarEntidades(gJuego, motor, observador);

        gJuego.dispose();

        dibujarOverlayEstado(g2d, motor);
        g2d.dispose();
    }

    private void dibujarMapa(Graphics g, JuegoEngine motor, ImageObserver observador) {
        Salida salida = motor.getSalida();
        if (salida != null) {
            Posicion posS = salida.getPosicion();
            Image spriteSalida = GestorRecursos.getSpriteSalida();
            g.drawImage(spriteSalida, posS.getX1(), posS.getY1(),
                    posS.getX2() - posS.getX1(), posS.getY2() - posS.getY1(), observador);
        }

        Image spriteMuro = GestorRecursos.getSpriteMuro();
        for (Muro muro : motor.getMuros()) {
            Posicion posM = muro.getPosicion();
            g.drawImage(spriteMuro, posM.getX1(), posM.getY1(),
                    posM.getX2() - posM.getX1(), posM.getY2() - posM.getY1(), observador);
        }
    }

    private void dibujarBuffos(Graphics g, JuegoEngine motor, ImageObserver observador) {
        for (ZonaBuffo zona : motor.getZonasBuffo()) {
            if (!zona.isRecolectado()) {
                Posicion posicion = zona.getPosicion();
                int x = posicion.getX1();
                int y = posicion.getY1();
                int ancho = posicion.getX2() - x;
                int alto = posicion.getY2() - y;

                Image sprite = obtenerSpriteBuffo(zona.getBuffo());
                if (sprite != null) {
                    g.drawImage(sprite, x, y, ancho, alto, observador);
                }
            }
        }
    }

    private Image obtenerSpriteBuffo(Buffo buffo) {
        if (buffo instanceof Cafe) {
            return GestorRecursos.getSpriteCafe();
        }
        if (buffo instanceof Aguinaldo) {
            return GestorRecursos.getSpriteAguinaldo();
        }
        if (buffo instanceof Corazon) {
            return GestorRecursos.getSpriteCorazon();
        }
        return null;
    }

    private void dibujarEntidades(Graphics g, JuegoEngine motor, ImageObserver observador) {
        Empleado jugador = motor.getJugador();
        if (jugador != null) {
            Posicion pos = jugador.getPosicion();
            int x = pos.getX1();
            int y = pos.getY1();
            int ancho = pos.getX2() - x;
            int alto = pos.getY2() - y;

            if (jugador.tieneEscudo()) {
                dibujarHaloEscudo(g, x, y, ancho, alto);
            }

            Image spriteJugador = GestorRecursos.getSpriteEmpleado(jugador.getDireccionVisual());
            g.drawImage(spriteJugador, x, y, ancho, alto, observador);
        }

        Jefe jefe = motor.getJefe();
        if (jefe != null) {
            Posicion pos = jefe.getPosicion();
            Image spriteJefe = GestorRecursos.getSpriteJefe(jefe.getDireccionActual(), jefe.estaAtacando());
            g.drawImage(spriteJefe, pos.getX1(), pos.getY1(),
                    pos.getX2() - pos.getX1(), pos.getY2() - pos.getY1(), observador);
        }

        for (Proyectil proyectil : motor.getProyectiles()) {
            if (proyectil.getEstaActivo()) {
                Posicion posicionProyectil = proyectil.getPosicion();
                Image spriteProyectil = GestorRecursos.getSpriteProyectil();
                g.drawImage(spriteProyectil, posicionProyectil.getX1(), posicionProyectil.getY1(),
                        posicionProyectil.getX2() - posicionProyectil.getX1(),
                        posicionProyectil.getY2() - posicionProyectil.getY1(), observador);
            }
        }
    }

    private void dibujarHaloEscudo(Graphics g, int x, int y, int ancho, int alto) {
        Graphics2D gHalo = (Graphics2D) g.create();
        gHalo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gHalo.setColor(new Color(0, 220, 255, 60));
        gHalo.fillOval(x - 4, y - 4, ancho + 8, alto + 8);
        gHalo.setColor(new Color(0, 255, 255, 190));
        gHalo.setStroke(new BasicStroke(1.5f));
        gHalo.drawOval(x - 4, y - 4, ancho + 8, alto + 8);
        gHalo.dispose();
    }

    private void dibujarHUD(Graphics g, JuegoEngine motor) {
        g.setColor(new Color(18, 18, 18));
        g.fillRect(0, 0, ANCHO_BASE, ALTO_HUD);

        g.setColor(new Color(55, 55, 55));
        g.drawLine(0, ALTO_HUD - 1, ANCHO_BASE, ALTO_HUD - 1);

        Empleado jugador = motor.getJugador();
        if (jugador == null) {
            return;
        }

        g.setFont(GestorRecursos.getFuenteArcade(10f));
        g.setColor(Color.WHITE);
        g.drawString("VIDAS:", 15, 22);

        Graphics2D g2d = (Graphics2D) g;
        int corazonesActuales = jugador.getSalud();
        for (int i = 0; i < Empleado.SALUD_DEFAULT; i++) {
            dibujarCorazon(g2d, 82 + (i * 22), 9, i < corazonesActuales);
        }

        int tiempoRestante = motor.getTiempoRestanteSegundos();
        g.setFont(GestorRecursos.getFuenteArcade(11f));
        g.setColor(tiempoRestante <= 10 ? Color.RED : Color.CYAN);
        g.drawString("TIEMPO: " + tiempoRestante + "s", 235, 23);

        g.setColor(new Color(255, 215, 0));
        g.drawString("PTS: " + motor.getPuntajeAcumulado(), 450, 23);

        g.setFont(GestorRecursos.getFuenteArcade(9f));
        g.setColor(Color.WHITE);
        g.drawString("VEL: " + jugador.getVelocidad(), 15, 43);

        if (jugador.tieneEscudo()) {
            g.setColor(new Color(50, 255, 100));
            g.drawString("ESCUDO: ACTIVO", 115, 43);
        } else {
            g.setColor(new Color(130, 130, 130));
            g.drawString("ESCUDO: INACTIVO", 115, 43);
        }

        for (Buffo buffo : jugador.getBuffosActivos()) {
            if (buffo instanceof Cafe) {
                g.setColor(new Color(255, 215, 0));
                g.drawString("+CAFE", 295, 43);
                break;
            }
        }

        g.setFont(GestorRecursos.getFuenteArcade(10f));
        if (motor.esNivelFinalActual()) {
            g.setColor(new Color(255, 215, 0));
            g.drawString("NIVEL: FINAL", 450, 43);
        } else {
            g.setColor(new Color(0, 220, 255));
            g.drawString("NIVEL: " + motor.getNumeroNivelActual() + "/" + motor.getTotalNiveles(), 450, 43);
        }
    }

    private void dibujarCorazon(Graphics2D g2d, int x, int y, boolean activo) {
        Graphics2D gCorazon = (Graphics2D) g2d.create();
        gCorazon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int[] xs = {x + 8, x + 1, x, x + 2, x + 5, x + 8, x + 11, x + 14, x + 16, x + 15};
        int[] ys = {y + 14, y + 7, y + 4, y + 1, y + 1, y + 4, y + 1, y + 1, y + 4, y + 7};

        if (activo) {
            gCorazon.setColor(new Color(235, 35, 60));
            gCorazon.fillPolygon(xs, ys, xs.length);
            gCorazon.setColor(new Color(255, 130, 145));
            gCorazon.fillRect(x + 3, y + 3, 2, 2);
            gCorazon.setColor(new Color(255, 80, 100));
            gCorazon.drawPolygon(xs, ys, xs.length);
        } else {
            gCorazon.setColor(new Color(45, 45, 45));
            gCorazon.fillPolygon(xs, ys, xs.length);
            gCorazon.setColor(new Color(95, 95, 95));
            gCorazon.drawPolygon(xs, ys, xs.length);
        }

        gCorazon.dispose();
    }

    private void dibujarOverlayEstado(Graphics g, JuegoEngine motor) {
        EstadoJuego estado = motor.getEstadoActual();
        if (estado == null || estado == EstadoJuego.JUGANDO) {
            return;
        }

        if (estado == EstadoJuego.PAUSADO) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, ANCHO_BASE, ALTO_BASE);

            g.setFont(GestorRecursos.getFuenteArcade(18f));
            g.setColor(Color.YELLOW);
            dibujarTextoCentrado(g, "JUEGO EN PAUSA", ALTO_BASE / 2 - 15);

            g.setFont(GestorRecursos.getFuenteArcade(9f));
            g.setColor(Color.WHITE);
            dibujarTextoCentrado(g, "Presiona 'P' o 'ESC' para continuar", ALTO_BASE / 2 + 15);
        } else if (estado.esFinDePartida()) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, ANCHO_BASE, ALTO_BASE);

            boolean esVictoria = (estado == EstadoJuego.VICTORIA);
            g.setFont(GestorRecursos.getFuenteArcade(18f));
            g.setColor(esVictoria ? Color.GREEN : Color.RED);

            String tituloEstado;
            if (!esVictoria) {
                tituloEstado = "GAME OVER";
            } else if (motor.esNivelFinalActual()) {
                tituloEstado = "¡NIVEL FINAL COMPLETADO!";
                g.setFont(GestorRecursos.getFuenteArcade(16f));
            } else {
                tituloEstado = "¡NIVEL " + motor.getNumeroNivelActual() + " COMPLETADO!";
            }
            dibujarTextoCentrado(g, tituloEstado, ALTO_BASE / 2 - 50);

            g.setFont(GestorRecursos.getFuenteArcade(8f));
            g.setColor(Color.WHITE);
            dibujarTextoCentrado(g, estado.getMensajeDescriptivo(), ALTO_BASE / 2 - 15);

            g.setFont(GestorRecursos.getFuenteArcade(10f));
            g.setColor(new Color(255, 215, 0));
            dibujarTextoCentrado(g, "Puntaje acumulado: " + motor.getPuntajeAcumulado(), ALTO_BASE / 2 + 15);

            g.setFont(GestorRecursos.getFuenteArcade(9f));
            if (esVictoria) {
                if (motor.haySiguienteNivel()) {
                    g.setColor(Color.CYAN);
                    dibujarTextoCentrado(g, "[ENTER] - SIGUIENTE NIVEL", ALTO_BASE / 2 + 50);
                    g.setColor(Color.LIGHT_GRAY);
                    dibujarTextoCentrado(g, "[M] - GUARDAR Y VOLVER AL MENU", ALTO_BASE / 2 + 75);
                } else {
                    g.setColor(new Color(255, 215, 0));
                    dibujarTextoCentrado(g, "¡FELICITACIONES! HAS COMPLETADO TODOS LOS NIVELES", ALTO_BASE / 2 + 50);
                    g.setColor(Color.CYAN);
                    dibujarTextoCentrado(g, "[ENTER] - CONTINUAR / [M] - VOLVER AL MENU", ALTO_BASE / 2 + 75);
                }
            } else if (estado == EstadoJuego.DERROTA_VIDA) {
                g.setColor(Color.YELLOW);
                dibujarTextoCentrado(g, "[R] - REINICIAR (NIVEL 1) / [M] - VOLVER AL MENU", ALTO_BASE / 2 + 60);
            } else {
                g.setColor(Color.YELLOW);
                dibujarTextoCentrado(g, "[R] - REINTENTAR NIVEL / [M] - VOLVER AL MENU", ALTO_BASE / 2 + 60);
            }
        }
    }

    private void dibujarTextoCentrado(Graphics g, String texto, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (ANCHO_BASE - fm.stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
    }
}
