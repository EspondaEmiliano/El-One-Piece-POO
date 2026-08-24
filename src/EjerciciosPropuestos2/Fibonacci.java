package EjerciciosPropuestos2;

import java.util.Scanner;

public class Fibonacci {
    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad de términos de Fibonacci a generar: ");
        int terminos = scanner.nextInt();

        int a = 0;
        int b = 1;
        int contador = 0;
        System.out.print("Los primeros "+terminos+" términos son:");

        while (contador < terminos) {
            if (contador < terminos - 1) {
                System.out.print(a + ", ");
            } else {
                System.out.print(a);
            }
            int siguiente = a + b;
            a = b;
            b = siguiente;
            contador++;
        }
        System.out.println();
        scanner.close();
    }
}
