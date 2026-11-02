package TpoEscapeCorporativo.servicios;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Servicio encargado de la reproducción de audio del juego.
 * Gestiona la música de fondo en un hilo separado de ejecución (Runnable)
 * y los efectos de sonido (SFX) mediante subprocesos asíncronos para no bloquear la interfaz.
 */
public class GestorAudio implements Runnable {

    // Rutas relativas a los archivos de audio
    private static final String RUTA_MUSICA_FONDO = "assets/sonidos/musica_fondo.wav";
    private static final String RUTA_SFX_IMPACTO = "assets/sonidos/impacto_pala.wav";

    // Instancia única (Patrón Singleton para control centralizado de audio)
    private static GestorAudio gestorAudio;

    // Volumen de 0.0 (silencio) a 1.0 (máximo). Por defecto 70%
    private float volumenActual = 0.70f;

    // Control del hilo de música
    private Thread hiloMusica;
    private volatile boolean enEjecucion;
    private Clip clipMusica;

    private GestorAudio() {
        this.enEjecucion = false;
    }

    public static synchronized GestorAudio getInstance() {
        if (gestorAudio == null) {
            gestorAudio = new GestorAudio();
        }
        return gestorAudio;
    }

    /**
     * Inicia la música de fondo en un hilo secundario independiente.
     * Si la música estaba pausada, la reanuda conservando el bucle continuo.
     */
    public synchronized void iniciarMusica() {
        if (hiloMusica != null && hiloMusica.isAlive()) {
            if (clipMusica != null && !clipMusica.isRunning()) {
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY); // Reanuda en bucle infinito
            }
            return;
        }

        enEjecucion = true;
        hiloMusica = new Thread(this, "Hilo-Musica-Fondo");
        hiloMusica.setDaemon(true); // Se cierra automáticamente al cerrar la JVM
        hiloMusica.start();
    }

    /**
     * Pausa la reproducción de la música de fondo.
     */
    public synchronized void pausarMusica() {
        if (clipMusica != null && clipMusica.isRunning()) {
            clipMusica.stop();
        }
    }

    /**
     * Detiene la música y finaliza el hilo de reproducción.
     */
    public synchronized void detenerMusica() {
        enEjecucion = false;
        if (clipMusica != null) {
            clipMusica.stop();
            clipMusica.close();
            clipMusica = null;
        }
        if (hiloMusica != null) {
            hiloMusica.interrupt();
            hiloMusica = null;
        }
    }

    /**
     * Ciclo de vida del hilo de música.
     * Incorpora manejo de excepciones con try-catch-finally.
     */
    @Override
    public void run() {
        AudioInputStream audioStream = null;
        File archivo = new File(RUTA_MUSICA_FONDO);

        if (!archivo.exists()) {
            System.err.println("Advertencia GestorAudio: No se encontró el archivo de música en: " + RUTA_MUSICA_FONDO);
            return;
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(archivo);
            clipMusica = AudioSystem.getClip();
            clipMusica.open(audioStream);
            aplicarVolumen();
            clipMusica.loop(Clip.LOOP_CONTINUOUSLY);

            // Mantiene el hilo activo mientras la música se reproduzca
            while (enEjecucion && !Thread.currentThread().isInterrupted()) {
                Thread.sleep(500);
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Error: Formato de audio no compatible en música de fondo: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O al leer la música de fondo: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Error: Línea de audio no disponible: " + e.getMessage());
        } catch (InterruptedException e) {
            // Interrupción controlada al detener la música
            Thread.currentThread().interrupt();
        } finally {
            if (audioStream != null) {
                try {
                    audioStream.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar AudioInputStream de música: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reproduce el sonido de impacto en un hilo rápido asíncrono para evitar
     * que la lógica física del juego se congele durante la lectura del archivo.
     * Aplica el volumen actual establecido en el gestor.
     */
    public static void reproducirImpactoPala() {
        GestorAudio gestor = getInstance();
        new Thread(() -> {
            File archivoSfx = new File(RUTA_SFX_IMPACTO);
            if (!archivoSfx.exists()) {
                // Si aún no copiaron el audio, suena un pitido por defecto del sistema
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }

            AudioInputStream streamSfx = null;
            try {
                streamSfx = AudioSystem.getAudioInputStream(archivoSfx);
                Clip clipSfx = AudioSystem.getClip();
                clipSfx.open(streamSfx);
                gestor.aplicarVolumenAClip(clipSfx); // Respeta el volumen maestro
                clipSfx.start();

                // Liberar el clip una vez que finalice la reproducción
                clipSfx.addLineListener(evento -> {
                    if (evento.getType() == LineEvent.Type.STOP) {
                        clipSfx.close();
                    }
                });
            } catch (Exception e) {
                System.err.println("Advertencia: No se pudo reproducir SFX de impacto: " + e.getMessage());
            } finally {
                if (streamSfx != null) {
                    try {
                        streamSfx.close();
                    } catch (IOException e) {
                        // Flujo cerrado
                    }
                }
            }
        }, "Hilo-SFX-Impacto").start();
    }

    /**
     * Modifica el volumen maestro del juego (música y efectos).
     *
     * @param porcentaje Valor entre 0.0f (mute) y 1.0f (máximo).
     */
    public synchronized void setVolumen(float porcentaje) {
        this.volumenActual = Math.max(0.0f, Math.min(1.0f, porcentaje));
        aplicarVolumen();
    }

    /**
     * Aplica el volumen actual sobre cualquier Clip mediante FloatControl en decibeles.
     *
     * @param clip Clip de audio (música o SFX) al que se le ajustará la ganancia.
     */
    public void aplicarVolumenAClip(Clip clip) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            try {
                FloatControl controlGanancia = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (volumenActual <= 0.001f) {
                    // Silencio total
                    controlGanancia.setValue(controlGanancia.getMinimum());
                } else {
                    // Conversión logarítmica de escala lineal a decibeles
                    float dB = (float) (Math.log10(volumenActual) * 20.0);
                    dB = Math.max(controlGanancia.getMinimum(), Math.min(dB, controlGanancia.getMaximum()));
                    controlGanancia.setValue(dB);
                }
            } catch (Exception e) {
                System.err.println("Advertencia: No se pudo ajustar el volumen: " + e.getMessage());
            }
        }
    }

    /**
     * Aplica el volumen actual al clip de música principal.
     */
    private void aplicarVolumen() {
        aplicarVolumenAClip(clipMusica);
    }

    /**
     * @return El porcentaje de volumen actual entre 0.0f y 1.0f.
     */
    public float getVolumenActual() {
        return volumenActual;
    }
}
