package EjerciciosPropuestos1;
import java.util.Scanner;

/**
 * Programa que determina si un año ingresado por el usuario es bisiesto.
 */
public class EjercicioAnioBisiesto {
    public static void main(String[] args) {
        int anio;

        // Se inicializa el Scanner para leer datos desde la consola
        Scanner scann = new Scanner(System.in);
        System.out.println("--- Sistema para Calcular el Anio ---");
        System.out.print("Ingrese el anio: ");
        anio = scann.nextInt();

        // 1. Debe ser divisible por 4 (anio % 4 == 0)
        // 2. No debe ser divisible por 100 (anio % 100 !=0)
        // 3. Excepción: Si es divisible por 100, también debe ser divisible por 400
        if ((anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0)) {
            System.out.println(anio + " es un año bisiesto.");
        } else {
            System.out.println(anio + " no es un año bisiesto.");
        }
        scann.close();
    }
}
