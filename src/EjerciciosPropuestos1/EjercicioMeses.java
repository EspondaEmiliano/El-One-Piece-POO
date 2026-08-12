package EjerciciosPropuestos1;
import java.util.Scanner;

/**
 * Programa que, dado el número de un mes ingresado por el usuario,
 * muestra su nombre y la cantidad de días que tiene.
 */
public class EjercicioMeses {
    public static void main(String[] args) {
        int mes;

        // Se inicializa el Scanner para leer datos desde la consola
        Scanner scann = new Scanner(System.in);

        System.out.println("--- Sistema de Consulta de Meses ---");
        System.out.println("Ingrese el número del mes (1 al 12): ");
        mes = scann.nextInt();

        // Evaluamos el número ingresado usando una cadena if - else if.
        // Al encontrar la primera condición verdadera, se ejecuta su bloque
        // y las condiciones restantes ya no se evalúan[cite: 3].
        if (mes == 1) {
            System.out.println("Enero - 31 días");
        } else if (mes == 2) {
            System.out.println("Febrero - 28 o 29 días");
        } else if (mes == 3) {
            System.out.println("Marzo - 31 días");
        } else if (mes == 4) {
            System.out.println("Abril - 30 días");
        } else if (mes == 5) {
            System.out.println("Mayo - 31 días");
        } else if (mes == 6) {
            System.out.println("Junio - 30 días");
        } else if (mes == 7) {
            System.out.println("Julio - 31 días");
        } else if (mes == 8) {
            System.out.println("Agosto - 31 días");
        } else if (mes == 9) {
            System.out.println("Septiembre - 30 días");
        } else if (mes == 10) {
            System.out.println("Octubre - 31 días");
        } else if (mes == 11) {
            System.out.println("Noviembre - 30 días");
        } else if (mes == 12) {
            System.out.println("Diciembre - 31 días");
        } else {
            // Si el número no coincide con ninguna de las opciones anteriores (no está entre 1 y 12),
            // se ejecuta esta rama por defecto para manejar el error[cite: 3].
            System.out.println("Número de mes inválido. Debe ingresar un valor entre 1 y 12.");
        }

        // Cerramos el objeto Scanner al finalizar para liberar recursos
        scann.close();
    }
}
